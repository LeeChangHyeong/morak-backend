# Morak Backend

Morak-Backend는 사용자 간의 소통과 상호작용을 위한 소셜 네트워킹 서비스의 백엔드 API입니다. 이 프로젝트는 게시물, 댓글, 채팅, 친구 관계 등 다양한 소셜 기능을 제공합니다.

## ✨ 주요 기능

*   **사용자 인증**: JWT를 이용한 안전한 이메일 가입 및 로그인/로그아웃 기능을 제공합니다.
*   **소셜 기능**:
    *   **친구**: 친구 요청, 수락/거절, 목록 조회, 차단 기능을 지원합니다.
    *   **게시물**: CRUD (생성, 조회, 수정, 삭제) 기능을 통해 사용자들이 자신의 이야기를 공유할 수 있습니다.
    *   **댓글**: 게시물에 대한 댓글 작성 및 조회 기능을 제공합니다.
    *   **좋아요**: 게시물과 댓글에 대한 '좋아요' 기능으로 상호작용을 촉진합니다.
*   **실시간 채팅**: WebSocket을 활용하여 사용자 간의 1:1 및 그룹 채팅 기능을 제공합니다.
*   **신고**: 불適切な 사용자, 게시물, 댓글을 신고할 수 있는 기능을 포함합니다.
*   **검색 및 정렬**: 다양한 조건으로 게시물을 검색하고 정렬할 수 있습니다.

## 🛠️ 기술 스택

*   **언어**: Java 17
*   **프레임워크**: Spring Boot 3, Spring Security, Spring Data JPA
*   **데이터베이스**: MySQL, Redis (캐싱 및 실시간 기능 지원)
*   **쿼리**: QueryDSL을 통한 동적 쿼리 작성
*   **인증**: JWT (JSON Web Token)
*   **실시간 통신**: WebSocket (STOMP)
*   **빌드 도구**: Gradle
*   **API 문서화**: Swagger (OpenAPI)

## 🚀 시작하기

### 1. 전제 조건

*   Java 17
*   Gradle 8.1.1 이상
*   MySQL
*   Redis

### 2. 프로젝트 클론

```bash
git clone https://github.com/your-username/morak-backend.git
cd morak-backend
```

### 3. 환경 설정

1.  `src/main/resources/application.properties.example` 파일을 복사하여 `application.properties` 파일을 생성합니다.
2.  `application.properties` 파일에 데이터베이스, Redis, JWT 시크릿 키 등 자신의 환경에 맞게 설정을 수정합니다.

    ```properties
    # Database
    spring.datasource.url=jdbc:mysql://localhost:3306/morak
    spring.datasource.username=your-db-username
    spring.datasource.password=your-db-password

    # Redis
    spring.data.redis.host=localhost
    spring.data.redis.port=6379

    # JWT
    jwt.secret.key=your-super-secret-key-that-is-long-enough
    ```

### 4. 빌드 및 실행

다음 명령어를 사용하여 애플리케이션을 빌드하고 실행합니다.

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

애플리케이션이 성공적으로 시작되면 `http://localhost:8080` 에서 실행됩니다.

## 📖 API 문서

애플리케이션 실행 후, 아래 URL에서 API 문서를 확인할 수 있습니다.

*   **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

## 📁 프로젝트 구조

```
src
└── main
    ├── java
    │   └── org
    │       └── brokong
    │           └── morakbackend
    │               ├── chat       # 채팅 관련 기능
    │               ├── comment    # 댓글 관련 기능
    │               ├── config     # 보안, DB, WebSocket 등 설정
    │               ├── friend     # 친구 및 차단 기능
    │               ├── global     # 예외 처리, JWT, 공통 응답 등
    │               ├── like       # 좋아요 기능
    │               ├── post       # 게시물 관련 기능
    │               ├── report     # 신고 기능
    │               └── user       # 사용자 및 인증 관련 기능
    └── resources
        ├── application.properties.example # 설정 예시 파일
        └── static
```
