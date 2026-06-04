#!/bin/bash
# ========================================================
# 재고콕 AWS ECS 인프라 셋업 스크립트
# 사용법: bash scripts/aws-setup.sh
# 실행 전: aws configure 로 IAM 키 설정 필요
# ========================================================

set -e

REGION="ap-northeast-2"
CLUSTER_NAME="jaegokok-cluster"
ECR_REPO="jaegokok-server"
SERVICE_NAME="jaegokok-service"
TASK_FAMILY="jaegokok-task"
CONTAINER_NAME="jaegokok-api"
DB_INSTANCE_NAME="jaegokok-mysql"
APP_INSTANCE_NAME="jaegokok-ecs-host"
KEY_PAIR_NAME="jaegokok-key"

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "✅ AWS Account: $ACCOUNT_ID (Region: $REGION)"

# ── 1. Key Pair ──────────────────────────────────────────
echo ""
echo "▶ 1. Key Pair 생성..."
aws ec2 create-key-pair \
  --key-name $KEY_PAIR_NAME \
  --region $REGION \
  --query 'KeyMaterial' \
  --output text > ~/.ssh/${KEY_PAIR_NAME}.pem
chmod 400 ~/.ssh/${KEY_PAIR_NAME}.pem
echo "✅ Key saved: ~/.ssh/${KEY_PAIR_NAME}.pem"

# ── 2. Security Groups ───────────────────────────────────
echo ""
echo "▶ 2. Security Groups 생성..."
VPC_ID=$(aws ec2 describe-vpcs \
  --filters "Name=isDefault,Values=true" \
  --region $REGION \
  --query 'Vpcs[0].VpcId' --output text)
echo "   VPC: $VPC_ID"

SG_DB=$(aws ec2 create-security-group \
  --group-name jaegokok-db-sg \
  --description "MySQL for jaegokok" \
  --vpc-id $VPC_ID \
  --region $REGION \
  --query 'GroupId' --output text)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_DB \
  --protocol tcp --port 3306 \
  --source-group $SG_DB \
  --region $REGION
echo "✅ DB Security Group: $SG_DB"

SG_APP=$(aws ec2 create-security-group \
  --group-name jaegokok-app-sg \
  --description "App server for jaegokok" \
  --vpc-id $VPC_ID \
  --region $REGION \
  --query 'GroupId' --output text)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_APP \
  --protocol tcp --port 22 --cidr 0.0.0.0/0 --region $REGION
aws ec2 authorize-security-group-ingress \
  --group-id $SG_APP \
  --protocol tcp --port 8080 --cidr 0.0.0.0/0 --region $REGION
aws ec2 authorize-security-group-ingress \
  --group-id $SG_APP \
  --protocol tcp --port 443 --cidr 0.0.0.0/0 --region $REGION
aws ec2 authorize-security-group-ingress \
  --group-id $SG_DB \
  --protocol tcp --port 3306 \
  --source-group $SG_APP \
  --region $REGION
echo "✅ App Security Group: $SG_APP"

# ── 3. MySQL EC2 인스턴스 ─────────────────────────────────
echo ""
echo "▶ 3. MySQL EC2 인스턴스 생성..."
DB_INSTANCE_ID=$(aws ec2 run-instances \
  --image-id ami-02c329a4b4aba6a48 \
  --instance-type t3.small \
  --key-name $KEY_PAIR_NAME \
  --security-group-ids $SG_DB \
  --region $REGION \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$DB_INSTANCE_NAME}]" \
  --user-data '#!/bin/bash
dnf update -y
dnf install -y mysql-server
systemctl start mysqld
systemctl enable mysqld
mysql -u root -e "CREATE DATABASE jaegokok CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -e "CREATE USER '\''jaegokok'\''@'\''%'\'' IDENTIFIED BY '\''${DB_PASSWORD:-changeme}'\'';"
mysql -u root -e "GRANT ALL PRIVILEGES ON jaegokok.* TO '\''jaegokok'\''@'\''%'\'';"
mysql -u root -e "FLUSH PRIVILEGES;"' \
  --query 'Instances[0].InstanceId' --output text)
echo "✅ DB EC2: $DB_INSTANCE_ID (시작 대기 중...)"
aws ec2 wait instance-running --instance-ids $DB_INSTANCE_ID --region $REGION
DB_PRIVATE_IP=$(aws ec2 describe-instances \
  --instance-ids $DB_INSTANCE_ID \
  --region $REGION \
  --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
echo "✅ DB Private IP: $DB_PRIVATE_IP"

# ── 4. ECR Repository ────────────────────────────────────
echo ""
echo "▶ 4. ECR Repository 생성..."
aws ecr create-repository \
  --repository-name $ECR_REPO \
  --region $REGION \
  --image-scanning-configuration scanOnPush=true 2>/dev/null || echo "   (이미 존재)"
ECR_URI="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${ECR_REPO}"
echo "✅ ECR: $ECR_URI"

# ── 5. ECS Cluster (EC2) ─────────────────────────────────
echo ""
echo "▶ 5. ECS Cluster 생성..."
aws ecs create-cluster \
  --cluster-name $CLUSTER_NAME \
  --region $REGION 2>/dev/null || echo "   (이미 존재)"

# ECS-optimized AMI (Amazon Linux 2023)
ECS_AMI=$(aws ssm get-parameters \
  --names /aws/service/ecs/optimized-ami/amazon-linux-2023/recommended/image_id \
  --region $REGION \
  --query 'Parameters[0].Value' --output text)

# IAM Instance Profile
aws iam create-role \
  --role-name ecsInstanceRole \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ec2.amazonaws.com"},"Action":"sts:AssumeRole"}]}' \
  2>/dev/null || true
aws iam attach-role-policy \
  --role-name ecsInstanceRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role \
  2>/dev/null || true
aws iam create-instance-profile \
  --instance-profile-name ecsInstanceProfile 2>/dev/null || true
aws iam add-role-to-instance-profile \
  --instance-profile-name ecsInstanceProfile \
  --role-name ecsInstanceRole 2>/dev/null || true
sleep 10

ECS_INSTANCE_ID=$(aws ec2 run-instances \
  --image-id $ECS_AMI \
  --instance-type t3.small \
  --key-name $KEY_PAIR_NAME \
  --security-group-ids $SG_APP \
  --iam-instance-profile Name=ecsInstanceProfile \
  --region $REGION \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$APP_INSTANCE_NAME}]" \
  --user-data "#!/bin/bash
echo ECS_CLUSTER=$CLUSTER_NAME >> /etc/ecs/ecs.config" \
  --query 'Instances[0].InstanceId' --output text)
echo "✅ ECS Host EC2: $ECS_INSTANCE_ID"
aws ec2 wait instance-running --instance-ids $ECS_INSTANCE_ID --region $REGION
APP_PUBLIC_IP=$(aws ec2 describe-instances \
  --instance-ids $ECS_INSTANCE_ID \
  --region $REGION \
  --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ App Public IP: $APP_PUBLIC_IP"

# ── 6. ECS Task Definition ───────────────────────────────
echo ""
echo "▶ 6. ECS Task Definition 등록..."

aws iam create-role \
  --role-name ecsTaskExecutionRole \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}' \
  2>/dev/null || true
aws iam attach-role-policy \
  --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy \
  2>/dev/null || true

cat > /tmp/task-definition.json << EOF
{
  "family": "$TASK_FAMILY",
  "executionRoleArn": "arn:aws:iam::${ACCOUNT_ID}:role/ecsTaskExecutionRole",
  "networkMode": "bridge",
  "containerDefinitions": [
    {
      "name": "$CONTAINER_NAME",
      "image": "${ECR_URI}:latest",
      "memory": 768,
      "portMappings": [{"containerPort": 8080, "hostPort": 8080, "protocol": "tcp"}],
      "essential": true,
      "environment": [
        {"name": "SPRING_PROFILES_ACTIVE", "value": "prod"},
        {"name": "MYSQL_HOST", "value": "$DB_PRIVATE_IP"},
        {"name": "MYSQL_PORT", "value": "3306"},
        {"name": "DB_NAME", "value": "jaegokok"},
        {"name": "MYSQL_USERNAME", "value": "jaegokok"},
        {"name": "MYSQL_PASSWORD", "value": "REPLACE_ME"},
        {"name": "APP_BASE_URL", "value": "https://api.jaegokok.com"},
        {"name": "TOSS_SECRET_KEY", "value": "REPLACE_ME"},
        {"name": "MAIL_USERNAME", "value": "REPLACE_ME"},
        {"name": "MAIL_PASSWORD", "value": "REPLACE_ME"}
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

aws logs create-log-group \
  --log-group-name /ecs/jaegokok \
  --region $REGION 2>/dev/null || true
aws ecs register-task-definition \
  --cli-input-json file:///tmp/task-definition.json \
  --region $REGION
echo "✅ Task Definition 등록 완료"

# ── 7. ECS Service ───────────────────────────────────────
echo ""
echo "▶ 7. ECS Service 생성..."
aws ecs create-service \
  --cluster $CLUSTER_NAME \
  --service-name $SERVICE_NAME \
  --task-definition $TASK_FAMILY \
  --desired-count 1 \
  --launch-type EC2 \
  --region $REGION
echo "✅ ECS Service 생성 완료"

# ── 완료 ─────────────────────────────────────────────────
echo ""
echo "========================================="
echo "✅ 인프라 셋업 완료!"
echo ""
echo "📋 다음 단계:"
echo "1. GitHub Secrets 설정:"
echo "   AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY"
echo ""
echo "2. Task Definition 환경변수 업데이트:"
echo "   DB_PASSWORD, TOSS_SECRET_KEY, MAIL_USERNAME, MAIL_PASSWORD"
echo ""
echo "3. Route 53 or DNS 설정:"
echo "   api.jaegokok.com → $APP_PUBLIC_IP"
echo ""
echo "4. ECR에 첫 이미지 push 후 ECS 서비스 재시작"
echo "========================================="
