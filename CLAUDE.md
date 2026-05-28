# jaegokok-server

## 프로젝트 개요

재고꼭 서버 - Spring Boot 3 + Java 21 기반 멀티모듈 REST API 서버

## 모듈 구조

```
jaegokok-common   — ApiResponse, ErrorCode, BusinessException, GlobalExceptionHandler
jaegokok-core     — JPA 엔티티 기반 클래스(BaseEntity), QueryDSL, JpaConfig
jaegokok-domain   — 도메인 서비스, DTO, JWT 처리
jaegokok-infra    — JPA Repository 구현체, Redis, QR 생성 등 외부 연동
jaegokok-api      — REST Controller, Spring Security, Swagger, 앱 진입점(main)
```

### 의존 방향

```
api → {common, core, domain, infra}
infra → {common, core, domain}
domain → {common, core}
core → common
```

## 기술 스택

| 항목 | 버전 |
|------|------|
| Java | 21 |
| Spring Boot | 3.4.5 |
| Gradle (Kotlin DSL) | - |
| MySQL | 8 |
| Redis | 7 |
| QueryDSL | 5.1.0 jakarta |
| jjwt | 0.12.6 |
| springdoc-openapi | 2.8.4 |
| ZXing | 3.5.3 |

## Git Convention

### 브랜치 전략

- `main` — 릴리즈 브랜치 (직접 push 금지)
- `develop` — 통합 브랜치 (**직접 push 금지**, 반드시 PR로만 머지)
- `feature/<기능명>` — 기능 개발 브랜치

### 흐름

```
feature/* → develop (PR) → main (릴리즈 PR)
```

### 커밋 메시지 형식

```
<type>: <subject>

feat     새로운 기능
fix      버그 수정
refactor 코드 리팩토링
chore    빌드, 설정 변경
docs     문서 수정
test     테스트 추가/수정
style    코드 포맷 변경
```

### 주의사항

- **develop 브랜치에 직접 push 금지** — 반드시 feature 브랜치에서 PR을 통해 머지
- PR 생성 후 코드 리뷰 필수

## 실행 방법

### 인프라 기동 (Docker)

```bash
docker-compose up -d
```

### 애플리케이션 실행

```bash
./gradlew :jaegokok-api:bootRun
```

### 빌드

```bash
./gradlew build
```

### 특정 모듈 빌드

```bash
./gradlew :jaegokok-api:build
```

## API 문서

서버 기동 후 아래 URL에서 확인:

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs (JSON): http://localhost:8080/api-docs
