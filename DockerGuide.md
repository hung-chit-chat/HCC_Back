# 도커로 테스트 서버 실행

1. 도커 다운로드
2. cmd 경로 /hung-chit-chat/testConnection/{실행할프로젝트} 로 이동
3. 모든 서비스 build 후 bulid/libs 폴더에 *.jar 가 있는지 확인
4. 명령어 실행 >docker-compose up --build


# 도커 명령어
1. docker-compose down -v            # 컨테이너와 볼륨 모두 삭제
2. docker-compose up                 # 컨테이너 재시작
3. docker system prune --volumes     # 모든 Docker 자원 정리
4. docker volume ls                  # 볼륨 상태 확인
5. docker volume rm <VOLUME_NAME>    # 도커 볼륨 삭제
6. docker rm <컨테이너 ID>             # 컨테이너 삭제