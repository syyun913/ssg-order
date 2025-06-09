# Docker 실행 가이드

## 사전 요구사항
- Docker Desktop 설치
  - [Windows](https://docs.docker.com/desktop/install/windows-install/)
  - [Mac](https://docs.docker.com/desktop/install/mac-install/)
  - [Linux](https://docs.docker.com/desktop/install/linux-install/)

## 프로젝트 실행 방법

### 1. 프로젝트 클론
```bash
git clone [repository-url]
cd order
```

### 2. 실행 스크립트 권한 부여
```bash
# Windows (PowerShell)
icacls docker-start.sh /grant Everyone:F

# Mac/Linux
chmod +x docker-start.sh
```

### 3. Docker 서비스 실행
```bash
# 서비스 시작
./docker-start.sh
```

### 4. 서비스 접속 정보
- 애플리케이션: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:ssg`
  - Username: `sa`
  - Password: `ssgorder1!`

### 5. 로그 확인
```bash
# 로그 확인
docker-compose logs -f
```

### 6. 서비스 중지
```bash
# 서비스 중지
docker-compose down
```

## 주의사항
- 포트 충돌이 발생할 경우 `docker-compose.yml`의 포트 설정을 수정해주세요.
- H2 Database는 인메모리 모드로 실행되므로 컨테이너가 중지되면 데이터가 초기화됩니다.
- Redis 데이터는 Docker 볼륨에 저장되며, `docker-compose down -v` 명령어로 삭제됩니다.

## 문제 해결
### 1. 포트 충돌
```bash
# 사용 중인 포트 확인
netstat -ano | findstr "8080"  # Windows
lsof -i :8080                  # Mac/Linux
```

### 2. 컨테이너 로그 확인
```bash
# 컨테이너 ID 확인
docker ps

# 로그 확인
docker logs [container-id]
```

### 3. 스크립트 실행 오류
```bash
# 스크립트 권한 확인
ls -l docker-start.sh

# 스크립트 권한 재설정
chmod +x docker-start.sh
```
