#!/bin/bash

cd /home/ubuntu/app

DOCKER_APP_NAME=spring

# blue가 실행 중인지 확인
EXIST_BLUE=$(docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps -q | xargs docker inspect -f '{{.State.Running}}')

# green이 실행 중이면 blue를 띄움
if [ "$EXIST_BLUE" != "true" ]; then
	echo "blue up"
	docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d --build

	sleep 30

	docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml down
	docker image prune -af # 사용하지 않는 이미지 삭제

# blue가 실행 중이면 green을 띄움
else
	echo "green up"
	docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d --build

	sleep 30

	docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml down
	docker image prune -af
fi