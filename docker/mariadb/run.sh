#!/bin/bash

echo "BUILD MARIA_DB IMAGE"
docker build -t my-custom-mariadb-image .

# 컨테이너 이름 설정
CONTAINER_NAME="my-mariadb-container"

# 컨테이너가 존재하는지 확인
if [ $(docker ps -a -q -f name=$CONTAINER_NAME) ]; then
    # 컨테이너가 존재하면 시작
    echo "Starting existing container $CONTAINER_NAME..."
    docker start $CONTAINER_NAME
else
    # 컨테이너가 존재하지 않으면 새로 생성 및 실행
    echo "Creating and running a new container $CONTAINER_NAME..."
    docker run -d --name $CONTAINER_NAME -p 3306:3306 my-custom-mariadb-image
fi
