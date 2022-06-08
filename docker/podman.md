# podman 安装


`sudo yum -y install podman`

# podman快速上手

```bash

# 查看版本
$ podman version

# 搜索镜像
$podman search nginx

# 拉取镜像
$ podman image pull docker.io/library/nginx

# 查看镜像
$ podman image ls

# 查看镜像详细信息
$ podman image inspect docker.io/library/nginx

# 运行容器
$ sudo podman container run -d -p 80:80 --name nginx --rm docker.io/library/nginx

$ sudo podman container ls
$ sudo podman container stop nginx
$ sudo podman container ls -a

```

# podman vs docker 不同

1. podman是Daemonless的，docker在执行任务的时候，必须依赖后台的docker daemon
2. podman 不需要root 用户或者root权限，所以更安全
3. podman 可以创建pod, pod的概念和k8s里定义的pod类似
4. podman 运行把镜像和容器存储在不同的地方，但是docker必须存储在docker engineer所在的本地
5. podman 是传统的fork-exec模式，而docker是client-server架构


切换用户：
useradd user
su -l user


# pod

```bash

$ podman pod --help
$ podman pod ps
POD ID      NAME        STATUS      CREATED     INFRA ID    # OF CONTAINERS

# 创建pod
$ podman pod create --name demo

# 删除pod
$ podman pod rm demo


$ podman pod create --name demo
185eaa958227ee4b1de0cb4e6292cdd8b5b91cc331d9ae04f63dc124d3a8ccc1
$ podman container run -d --name test1 --pod demo docker.io/library/busybox ping 8.8.8.8
682d4e9ebb0a73b542ca89139e91c486b8b53e76f12b0d483eba44bcb08436ca


```













