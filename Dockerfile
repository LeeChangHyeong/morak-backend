FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 파일들 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# 의존성 다운로드를 위한 빌드 (캐시 최적화)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew build -x test --no-daemon

# JAR 파일을 실행 가능한 위치로 복사 (수정된 부분)
RUN cp build/libs/*-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 변경 테스트