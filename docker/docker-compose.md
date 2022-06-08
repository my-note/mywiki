# docker-compose

## 安装Docker-compose

```bash
https://github.com/docker/compose
$ mv docker-compose-linux-x86_64 docker-compose
$ chmod +x docker-compose
$ sudo mv docker-compose /usr/local/bin/
$ docker-compose --version
```

## 文件的结构和版本

docker compose 语法说明：https://docs.docker.com/compose/compose-file/



推荐文件名：compose.yaml


基本语法结构

```yaml
version: "3.8"

services: # 容器
  servicename: # 服务名字，这个名字也是内部bridge网络可以使用的dns name
    container_name: # 可选，不推荐。指定容器的名字，默认使用项目名_服务名_num
    image: # 镜像的名字
    command: # 可选，如果设置，可以覆盖默认镜像里的CMD 命令
    environment: # 可选，相当于docker run 里的 --env
      - REDIS_HOST=redis-server
      - REDIS_PASS=${REDIS_PASSWORD} # 环境变量里面也可以定义变量，默认读.env文件
    volumes: # 可选，相当于docker run 里的 -v
    networks: # 可选，相当于 docker run 里面的 --network
    ports: # 可选，相当于docker run里的-p
  servicename2:

volumes: # 可选，相当于 docker volume create

networks: # 可选，相当于 docker network create
  
  
```




## 常用命令

```bash
# 验证配置文件
docker-compose config
docker-compose --env-file=myenv config

# 后台启动
docker-compose up -d

# 查看服务
docker-compose ps

# 停止服务&容器
docker-compose stop 


# 删除停止的服务 & 容器
docker-compose rm

# 更新操作常用命令
docker-compose up -d --remove-orphans
docker-compose restart


# 水平扩展
docker-compose up -d --scale flask=3
```


























