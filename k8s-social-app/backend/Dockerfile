# 멀티 스테이지 빌드를 사용하여 이미지 크기 최적화
FROM gradle:8.4-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 스크립트 복사 (캐싱 최적화)
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (소스 코드 변경 시에도 재다운로드하지 않음)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드 (테스트 스킵으로 빌드 시간 단축)
RUN ./gradlew bootJar --no-daemon -x test

# 런타임 이미지 생성
FROM eclipse-temurin:17-jre

# 시스템 업데이트 및 필요한 패키지 설치
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 애플리케이션 실행을 위한 사용자 생성 (보안 강화)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 파일 소유권 변경
RUN chown appuser:appuser app.jar

# 비권한 사용자로 실행
USER appuser

# 포트 노출
EXPOSE 8080

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# 애플리케이션 실행
# JVM 최적화 옵션 추가
ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Xms256m", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]