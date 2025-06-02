#!/bin/bash

echo "SSG Order 서비스를 시작합니다..."

# Docker Compose로 서비스 시작
docker-compose up --build -d

echo "서비스가 시작되었습니다!"
echo "애플리케이션: http://localhost:8080"
echo "Redis: localhost:6379"
echo ""
echo "로그 확인: docker-compose logs -f"
echo "서비스 중지: docker-compose down"