#!/bin/bash
# ========================================================
# 재고콕 AWS ECS 인프라 셋업 스크립트
# 사용법: bash scripts/aws-setup.sh
# 필수: aws configure 완료, 아래 환경변수 설정
#
# export DB_PASSWORD=your_db_password
# export REDIS_PASSWORD=your_redis_password (빈 문자열 가능)
# export JWT_SECRET=your-256bit-secret-key
# export TOSS_SECRET_KEY=test_sk_xxxxx
# export MAIL_USERNAME=yourmail@gmail.com
# export MAIL_PASSWORD=your-app-password
# export S3_BUCKET=jaegokok-prod
# ========================================================

set -e

REGION="ap-northeast-2"
CLUSTER_NAME="jaegokok-cluster"
ECR_REPO="jaegokok-server"
SERVICE_NAME="jaegokok-service"
TASK_FAMILY="jaegokok-task"
CONTAINER_NAME="jaegokok-api"
KEY_PAIR_NAME="jaegokok-key"
SECRET_NAME="jaegokok/prod"

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "✅ AWS Account: $ACCOUNT_ID (Region: $REGION)"

# ── 1. Secrets Manager ───────────────────────────────────
echo ""
echo "▶ 1. AWS Secrets Manager에 비밀값 저장..."
aws secretsmanager create-secret \
  --name "$SECRET_NAME" \
  --description "재고콕 프로덕션 시크릿" \
  --secret-string "{
    \"MYSQL_PASSWORD\": \"${DB_PASSWORD:?DB_PASSWORD 필요}\",
    \"REDIS_PASSWORD\": \"${REDIS_PASSWORD:-}\",
    \"JWT_SECRET\": \"${JWT_SECRET:?JWT_SECRET 필요}\",
    \"TOSS_SECRET_KEY\": \"${TOSS_SECRET_KEY:?TOSS_SECRET_KEY 필요}\",
    \"MAIL_USERNAME\": \"${MAIL_USERNAME:?MAIL_USERNAME 필요}\",
    \"MAIL_PASSWORD\": \"${MAIL_PASSWORD:?MAIL_PASSWORD 필요}\"
  }" \
  --region $REGION 2>/dev/null || \
aws secretsmanager update-secret \
  --secret-id "$SECRET_NAME" \
  --secret-string "{
    \"MYSQL_PASSWORD\": \"${DB_PASSWORD}\",
    \"REDIS_PASSWORD\": \"${REDIS_PASSWORD:-}\",
    \"JWT_SECRET\": \"${JWT_SECRET}\",
    \"TOSS_SECRET_KEY\": \"${TOSS_SECRET_KEY}\",
    \"MAIL_USERNAME\": \"${MAIL_USERNAME}\",
    \"MAIL_PASSWORD\": \"${MAIL_PASSWORD}\"
  }" \
  --region $REGION

SECRET_ARN=$(aws secretsmanager describe-secret \
  --secret-id "$SECRET_NAME" \
  --region $REGION \
  --query ARN --output text)
echo "✅ Secrets Manager ARN: $SECRET_ARN"

# ── 2. Key Pair ──────────────────────────────────────────
echo ""
echo "▶ 2. Key Pair 생성..."
aws ec2 create-key-pair \
  --key-name $KEY_PAIR_NAME \
  --region $REGION \
  --query 'KeyMaterial' \
  --output text > ~/.ssh/${KEY_PAIR_NAME}.pem 2>/dev/null || echo "   (이미 존재)"
chmod 400 ~/.ssh/${KEY_PAIR_NAME}.pem 2>/dev/null || true
echo "✅ Key: ~/.ssh/${KEY_PAIR_NAME}.pem"

# ── 3. VPC & Security Groups ─────────────────────────────
echo ""
echo "▶ 3. Security Groups 생성..."
VPC_ID=$(aws ec2 describe-vpcs \
  --filters "Name=isDefault,Values=true" \
  --region $REGION \
  --query 'Vpcs[0].VpcId' --output text)
echo "   VPC: $VPC_ID"

create_sg() {
  local name=$1 desc=$2
  local id=$(aws ec2 describe-security-groups \
    --filters "Name=group-name,Values=$name" "Name=vpc-id,Values=$VPC_ID" \
    --region $REGION --query 'SecurityGroups[0].GroupId' --output text 2>/dev/null)
  if [ "$id" = "None" ] || [ -z "$id" ]; then
    id=$(aws ec2 create-security-group \
      --group-name "$name" --description "$desc" \
      --vpc-id $VPC_ID --region $REGION \
      --query 'GroupId' --output text)
  fi
  echo $id
}

SG_DB=$(create_sg "jaegokok-db-sg" "jaegokok DB")
SG_APP=$(create_sg "jaegokok-app-sg" "jaegokok App")

aws ec2 authorize-security-group-ingress --group-id $SG_APP --protocol tcp --port 22 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-id $SG_APP --protocol tcp --port 8080 --cidr 0.0.0.0/0 --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-id $SG_DB --protocol tcp --port 3306 --source-group $SG_APP --region $REGION 2>/dev/null || true
aws ec2 authorize-security-group-ingress --group-id $SG_DB --protocol tcp --port 6379 --source-group $SG_APP --region $REGION 2>/dev/null || true
echo "✅ SG_DB=$SG_DB / SG_APP=$SG_APP"

# ── 4. MySQL + Redis EC2 (단일 인스턴스) ─────────────────
echo ""
echo "▶ 4. DB/Redis EC2 (t3.small) 생성..."
DB_INSTANCE_ID=$(aws ec2 run-instances \
  --image-id ami-02c329a4b4aba6a48 \
  --instance-type t3.small \
  --key-name $KEY_PAIR_NAME \
  --security-group-ids $SG_DB $SG_APP \
  --region $REGION \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=jaegokok-db}]" \
  --user-data "#!/bin/bash
dnf update -y
dnf install -y mysql-server redis6
systemctl enable --now mysqld
mysql -u root -e \"CREATE DATABASE IF NOT EXISTS jaegokok CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\"
mysql -u root -e \"CREATE USER IF NOT EXISTS 'jaegokok'@'%' IDENTIFIED BY '${DB_PASSWORD}';\"
mysql -u root -e \"GRANT ALL PRIVILEGES ON jaegokok.* TO 'jaegokok'@'%'; FLUSH PRIVILEGES;\"
sed -i 's/^bind .*/bind 0.0.0.0/' /etc/redis.conf
systemctl enable --now redis" \
  --query 'Instances[0].InstanceId' --output text)

echo "   DB EC2: $DB_INSTANCE_ID (시작 대기 중...)"
aws ec2 wait instance-running --instance-ids $DB_INSTANCE_ID --region $REGION
DB_PRIVATE_IP=$(aws ec2 describe-instances \
  --instance-ids $DB_INSTANCE_ID --region $REGION \
  --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
echo "✅ DB Private IP: $DB_PRIVATE_IP"

# ── 5. ECR Repository ────────────────────────────────────
echo ""
echo "▶ 5. ECR Repository 생성..."
aws ecr create-repository \
  --repository-name $ECR_REPO \
  --region $REGION \
  --image-scanning-configuration scanOnPush=true 2>/dev/null || true
ECR_URI="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${ECR_REPO}"
echo "✅ ECR: $ECR_URI"

# ── 6. IAM Roles ─────────────────────────────────────────
echo ""
echo "▶ 6. IAM Roles 생성..."

# ECS Instance Role
aws iam create-role --role-name ecsInstanceRole \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ec2.amazonaws.com"},"Action":"sts:AssumeRole"}]}' 2>/dev/null || true
aws iam attach-role-policy --role-name ecsInstanceRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role 2>/dev/null || true
aws iam create-instance-profile --instance-profile-name ecsInstanceProfile 2>/dev/null || true
aws iam add-role-to-instance-profile --instance-profile-name ecsInstanceProfile --role-name ecsInstanceRole 2>/dev/null || true

# ECS Task Execution Role (Secrets Manager + ECR 접근)
aws iam create-role --role-name ecsTaskExecutionRole \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}' 2>/dev/null || true
aws iam attach-role-policy --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy 2>/dev/null || true
aws iam attach-role-policy --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/SecretsManagerReadWrite 2>/dev/null || true

# ECS Task Role (S3 접근)
aws iam create-role --role-name ecsTaskRole \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}' 2>/dev/null || true
aws iam attach-role-policy --role-name ecsTaskRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess 2>/dev/null || true

sleep 10
echo "✅ IAM Roles 완료"

# ── 7. ECS Cluster (EC2) ─────────────────────────────────
echo ""
echo "▶ 7. ECS Cluster + EC2 호스트 생성..."
aws ecs create-cluster --cluster-name $CLUSTER_NAME --region $REGION 2>/dev/null || true

ECS_AMI=$(aws ssm get-parameters \
  --names /aws/service/ecs/optimized-ami/amazon-linux-2023/recommended/image_id \
  --region $REGION --query 'Parameters[0].Value' --output text)

ECS_INSTANCE_ID=$(aws ec2 run-instances \
  --image-id $ECS_AMI \
  --instance-type t3.small \
  --key-name $KEY_PAIR_NAME \
  --security-group-ids $SG_APP \
  --iam-instance-profile Name=ecsInstanceProfile \
  --region $REGION \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=jaegokok-ecs-host}]" \
  --user-data "#!/bin/bash
echo ECS_CLUSTER=$CLUSTER_NAME >> /etc/ecs/ecs.config" \
  --query 'Instances[0].InstanceId' --output text)

aws ec2 wait instance-running --instance-ids $ECS_INSTANCE_ID --region $REGION
APP_PUBLIC_IP=$(aws ec2 describe-instances \
  --instance-ids $ECS_INSTANCE_ID --region $REGION \
  --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ ECS Host: $ECS_INSTANCE_ID ($APP_PUBLIC_IP)"

# ── 8. CloudWatch Log Group ──────────────────────────────
aws logs create-log-group --log-group-name /ecs/jaegokok --region $REGION 2>/dev/null || true

# ── 9. ECS Task Definition (Secrets Manager 연동) ────────
echo ""
echo "▶ 8. ECS Task Definition 등록..."
cat > /tmp/task-definition.json << EOF
{
  "family": "$TASK_FAMILY",
  "executionRoleArn": "arn:aws:iam::${ACCOUNT_ID}:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::${ACCOUNT_ID}:role/ecsTaskRole",
  "networkMode": "bridge",
  "containerDefinitions": [
    {
      "name": "$CONTAINER_NAME",
      "image": "${ECR_URI}:latest",
      "memory": 896,
      "portMappings": [{"containerPort": 8080, "hostPort": 8080, "protocol": "tcp"}],
      "essential": true,
      "environment": [
        {"name": "SPRING_PROFILES_ACTIVE", "value": "prod,datasource,redis,security,s3,mail,toss,docs"},
        {"name": "MYSQL_HOST",             "value": "$DB_PRIVATE_IP"},
        {"name": "MYSQL_PORT",             "value": "3306"},
        {"name": "DB_NAME",                "value": "jaegokok"},
        {"name": "MYSQL_USERNAME",         "value": "jaegokok"},
        {"name": "REDIS_HOST",             "value": "$DB_PRIVATE_IP"},
        {"name": "REDIS_PORT",             "value": "6379"},
        {"name": "S3_BUCKET",              "value": "${S3_BUCKET:-jaegokok-prod}"},
        {"name": "S3_REGION",              "value": "$REGION"},
        {"name": "APP_BASE_URL",           "value": "https://api.jaegokok.com"},
        {"name": "JWT_ACCESS_EXPIRY_MS",   "value": "3600000"},
        {"name": "JWT_REFRESH_EXPIRY_MS",  "value": "604800000"}
      ],
      "secrets": [
        {"name": "MYSQL_PASSWORD",  "valueFrom": "${SECRET_ARN}:MYSQL_PASSWORD::"},
        {"name": "REDIS_PASSWORD",  "valueFrom": "${SECRET_ARN}:REDIS_PASSWORD::"},
        {"name": "JWT_SECRET",      "valueFrom": "${SECRET_ARN}:JWT_SECRET::"},
        {"name": "TOSS_SECRET_KEY", "valueFrom": "${SECRET_ARN}:TOSS_SECRET_KEY::"},
        {"name": "MAIL_USERNAME",   "valueFrom": "${SECRET_ARN}:MAIL_USERNAME::"},
        {"name": "MAIL_PASSWORD",   "valueFrom": "${SECRET_ARN}:MAIL_PASSWORD::"}
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/jaegokok",
          "awslogs-region": "$REGION",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
EOF

aws ecs register-task-definition \
  --cli-input-json file:///tmp/task-definition.json \
  --region $REGION > /dev/null
echo "✅ Task Definition 등록"

# ── 10. ECS Service ──────────────────────────────────────
aws ecs create-service \
  --cluster $CLUSTER_NAME \
  --service-name $SERVICE_NAME \
  --task-definition $TASK_FAMILY \
  --desired-count 1 \
  --launch-type EC2 \
  --region $REGION > /dev/null 2>/dev/null || true
echo "✅ ECS Service 생성"

# ── 완료 ─────────────────────────────────────────────────
echo ""
echo "==========================================="
echo "✅ 인프라 셋업 완료!"
echo ""
echo "📋 GitHub Secrets 추가 필요:"
echo "   AWS_ACCESS_KEY_ID"
echo "   AWS_SECRET_ACCESS_KEY"
echo ""
echo "🌐 DNS 설정:"
echo "   api.jaegokok.com → $APP_PUBLIC_IP"
echo ""
echo "🚀 첫 배포: develop → main PR 머지"
echo "==========================================="
