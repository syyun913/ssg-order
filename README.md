# SSG Order Service - 사전 과제

## 프로젝트 소개
SSG Order는 SSG의 주문 사전 과제를 구현한 서비스입니다. 주문 생성, 조회, 취소 등의 기능을 제공하며, 도메인 주도 설계(DDD) 패턴을 적용하여 구현되었습니다.

### 주요 특징
- 도메인 주도 설계(DDD) 기반의 아키텍처
- 계층형 구조를 통한 관심사 분리
- JWT 기반의 보안 인증
- Redis를 활용한 캐싱 및 분산락
- Docker 컨테이너화 지원

## 기술 스택

### 백엔드
- **언어 & 프레임워크**
  - Java 17
  - Spring Boot 3.5.0
  - Spring Security
  - Spring Data JPA
  - Spring Data Redis

- **데이터 접근**
  - JPA/Hibernate
  - Redis Client

- **보안**
  - JWT (JSON Web Token)
  - Spring Security
  - Jasypt (민감 정보 암호화)

### 프론트엔드
- **템플릿 엔진**
  - Thymeleaf

### 데이터베이스
- **메인 DB**
  - H2 Database (개발용)
  - JPA/Hibernate

- **캐시**
  - Redis
  - Spring Data Redis

### 인프라
- **컨테이너화**
  - Docker
  - Docker Compose

- **빌드 도구**
  - Gradle
  - Gradle Wrapper

### 개발 도구
- **API 문서화**
  - Swagger/OpenAPI 3.0

- **테스트**
  - JUnit 5
  - Spring Test
  - ArchUnit (아키텍처 테스트)

## 프로젝트 구조
```
src/main/java/com/ssg/order/
├── api/                    # API 계층 (Controller, Service, DTO)
│   ├── order/             # 주문 관련 API
│   ├── product/           # 상품 관련 API
│   └── user/              # 사용자 관련 API
├── domain/                 # 도메인 계층 (Entity, Repository Interface, UseCase)
│   ├── common/            # 공통 모듈
│   │   ├── annotation/    # 커스텀 어노테이션
│   │   └── util/          # 유틸리티 클래스
│   └── domain/            # 도메인 모듈
│       ├── order/         # 주문 도메인
│       │   ├── repository/# 주문 저장소 인터페이스
│       │   └── usecase/   # 주문 유스케이스
│       ├── product/       # 상품 도메인
│       └── user/          # 사용자 도메인
└── infrastructure/                 # 인프라 계층 (Repository 구현체, 외부 서비스 연동)
    ├── persistence/       # 영속성 관련 구현체
    │   ├── order/        # 주문 영속성
    │   ├── product/      # 상품 영속성
    │   └── user/         # 사용자 영속성
    └── jasypt/           # 암호화 설정
```

## 아키텍처
이 프로젝트는 계층형 아키텍처와 도메인 주도 설계(DDD)를 결합하여 구현되었습니다:

### 1. API 계층
- **Controller**: HTTP 요청/응답 처리
  - RESTful API 엔드포인트 제공
  - 요청 유효성 검증
  - 응답 데이터 변환
- **Service**: 비즈니스 로직 조정
  - 트랜잭션 관리
  - 도메인 로직 조합
  - 예외 처리
- **DTO**: 데이터 전송 객체
  - 요청/응답 데이터 구조 정의
  - 데이터 변환 로직

### 2. 도메인 계층
- **Repository Interface**: 데이터 접근 계층
  - 도메인 객체 저장/조회 인터페이스
  - 영속성 계층 독립성 보장
- **UseCase**: 핵심 비즈니스 로직
  - 도메인 간 역할 분리
  - 도메인 규칙 구현

### 3. 인프라 계층
- **Repository 구현체**: JPA 기반 데이터 접근
  - 데이터베이스 연동
  - 쿼리 최적화
- **외부 서비스 연동**: Redis, 외부 API 등
  - 캐시 관리
  - 외부 시스템 통합

## 주요 기능
### 주문 관리
- 주문 생성 및 상태 관리
  - Redis 기반 분산락을 통한 동시성 제어
  - 동일 사용자의 중복 주문 방지 (60초 락 타임)
  - 재고 관리의 정합성 보장
- 주문 내역 조회
- 주문 취소 및 환불 처리

### 상품 관리
- 상품 재고 관리
- 상품 정보 조회

### 사용자 관리
- JWT 기반 인증
- 사용자 권한 관리
- 사용자 정보 관리

## 보안
### 인증 및 권한
- JWT 기반 토큰 인증
- Spring Security를 통한 접근 제어

### 데이터 보안
- Jasypt를 통한 민감 정보 암호화
- Spring Security를 통한 보안 처리

## 프로젝트 실행
프로젝트는 Docker를 통해 쉽게 실행할 수 있습니다. 자세한 실행 방법은 [Docker 실행 가이드](DOCKER_GUIDE.md)를 참고해주세요.

## 테스트
### 단위 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.ssg.order.*Test"
```

## CI/CD
GitHub Actions를 통해 자동화된 테스트 및 빌드가 수행됩니다:

### PR 테스트
- develop, master 브랜치에 대한 PR 테스트
- 단위 테스트 실행
- 빌드 검증
- 테스트 커버리지 리포트 생성

### 워크플로우 파일
- [PR 테스트 워크플로우](.github/workflows/pr-test-work.yml)

## 구현 포인트
1. **아키텍처 설계**
   - 도메인 주도 설계(DDD) 적용
   - 계층형 구조를 통한 관심사 분리
   - 확장성과 유지보수성 고려

2. **코드 품질**
   - 클린 코드 원칙 준수
   - 테스트 코드 작성
   - 예외 처리 및 로깅

3. **기술적 역량**
   - Spring Boot 활용
   - JPA/Hibernate 이해
   - 보안 처리
   - Docker 활용

4. **비즈니스 로직**
   - 도메인 규칙 구현
   - 트랜잭션 관리
   - 데이터 정합성 보장
