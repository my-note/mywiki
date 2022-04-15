# Docker install MySql


## 1. docker run

```bash
mkdir ~/mysql
cd ~/mysql
docker pull mysql
docker run --name mysql \
    -e MYSQL_ROOT_PASSWORD=123456 \
    -p 3306:3306 \
    -v $PWD/DATA:/var/lib/mysql \
    -v $PWD/logs:/logs \
    -v $PWD/conf:/etc/mysql/conf.d \
    -d mysql \
    --character-set-server=utf8mb4 \
    --collation-server=utf8mb4_unicode_ci \
    --default-authentication-plugin=mysql_native_password \
    --secure_file_priv=/var/lib/mysql
docker exec -it mysql /bin/bash
mysql -u root -p
use mysql
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
create database sync_api  DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_general_ci;
```


## 2. shell脚本

```shell
#!/usr/bin/env bash


set -u
set -e

containerName=$1
action=$2

echo "========= ${containerName} ${action} ========";sleep 3




function start() {
    echo "start";sleep 3
    MYSQL_CONTAINER_RUNNING=$(docker ps --format "{{.Names}}"|grep "${containerName}" | wc -l)


    if [ "${MYSQL_CONTAINER_RUNNING}" == "1" ]; then
        echo "${containerName} is running!"
        exit 0
    fi

    MYSQL_CONTAINER_EXIST=$(docker ps -a --format "{{.Names}}"|grep "${containerName}" | wc -l)

    if [ "${MYSQL_CONTAINER_EXIST}" == "1" ]; then
        echo "${containerName} container exist! start container";sleep 3
        docker start "${containerName}"
        exit 0
    fi


    MYSQL_DATA_VOLUME_EXIST=$(docker volume ls --format "{{.Name}}"|grep mysql-data | wc -l)
    MYSQL_LOG_VOLUME_EXIST=$(docker volume ls --format "{{.Name}}"|grep mysql-log | wc -l)

    if [ "${MYSQL_DATA_VOLUME_EXIST}" != "1" ]; then
        echo "mysql-data volume not exist. create"; sleep 3
        docker volume create --driver local mysql-data
        echo "mysql-data volume create success"; sleep 3
    fi
    if [ "${MYSQL_LOG_VOLUME_EXIST}" != "1" ]; then
        echo "mysql-log volume not exist.create";sleep 3
        docker volume create --driver local mysql-log
        echo "mysql-log volume create success"; sleep 3
    fi

    docker pull mysql
    docker run --name mysql \
        -e MYSQL_ROOT_PASSWORD=123456 \
        -p 3306:3306 \
        -v mysql-data:/var/lib/mysql \
        -v mysql-log:/logs \
        -d mysql \
        --character-set-server=utf8mb4 \
        --collation-server=utf8mb4_unicode_ci \
        --default-authentication-plugin=mysql_native_password
}

function stop() {
    echo "docker stop mysql";sleep 3
    docker stop mysql
}

function rm() {
    echo "docker stop mysql";sleep 3
    docker stop mysql || true
    echo "docker rm mysql";sleep 3
    docker rm mysql || true
    echo "docker volume rm mysql-data";sleep 3
    docker volume rm mysql-data || true
    echo "docker volume rm mysql-log";sleep 3
    docker volume rm mysql-log || true
}


if [ "${action}" == "start" ]
then
    start
elif [ "${action}" == "stop" ]
then
    stop
elif [ "${action}" == "rm" ]
then
    rm
else
   echo "input err"
fi
```


## 3. docker-compose



```yaml
version: '3.7'
services:
  mysql:
    image: mysql:latest
    command: ["--default-authentication-plugin=mysql_native_password", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]
    ports:
      - 3306:3306
    volumes:
      - "mysql-data:/var/lib/mysql"
      - "mysql-log:/logs"
    environment:
      MYSQL_ROOT_PASSWORD: example
    networks:
      - app-network
# docker network create --driver overlay app-network
networks:
  app-network:
    external: true
# docker volume create --driver local mysql-data =>  "Mountpoint": "/var/lib/docker/volumes/mysql-data/_data"
volumes:
  mysql-data:
    external: true
    name: mysql-data
  mysql-log:

```

















