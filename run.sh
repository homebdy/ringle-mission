#!/bin/bash
set -e

echo "1. Docker Compose"
docker-compose up -d --wait
#sleep 5

echo "2. Spring Boot 애플리케이션 실행형 JAR 빌드"
./gradlew bootJar -x test

JAR_FILE=$(find build/libs -name "*SNAPSHOT.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "실행형 JAR 파일을 찾을 수 없습니다."
  exit 1
fi

echo "3. 애플리케이션 실행: $JAR_FILE"
java -jar "$JAR_FILE"
