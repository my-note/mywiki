# CentOS7安装JDK11保姆级教程

原创2022-01-18 20:14·[字母哥课堂](https://www.toutiao.com/c/user/token/MS4wLjABAAAAwiLOePZy30FRIklS6RfQrwIGwzyj2evd3_BtWDxuD4Q/?source=tuwen_detail)

## 卸载OpenJDK

一般来说，CentOS linux会自带OpenJdk,用命令java -version，可能会有下面的信息显示（如果没有该信息，这一小节就不用关注了）：

```
java version "1.6.0"

OpenJDK Runtime Environment (build 1.6.0-b09)
OpenJDK 64-Bit Server VM (build 1.6.0-b09, mixed mode)
```

OpenJdk也是java JDK，但是我们和我们通常使用的Oracle JDK还是有区别的。在生产环境下，我们还是使用稳定、高效的Oracle JDK版本。所以先卸载掉OpenJdk,再安装oracle公司的jdk.

```
# 通过该命令查找安装包
rpm -qa | grep java

# 或者通过yum命令查找安装包
yum list installed |grep java
```

通过上面的命令查找java jdk安装包，显示如下信息，可以看到安装的openjdk：

```
java-1.4.2-gcj-compat-1.4.2.0-40jpp.115
java-1.6.0-openjdk-1.6.0.0-1.7.b09.el5
```

卸载命令：

```
rpm -e --nodeps java-1.4.2-gcj-compat-1.4.2.0-40jpp.115
rpm -e --nodeps java-1.6.0-openjdk-1.6.0.0-1.7.b09.el5
```

如果出现找不到openjdk的话，那么还可以这样卸载

```
yum -y remove java-1.4.2-gcj-compat-1.4.2.0-40jpp.115
yum -y remove java-1.6.0-openjdk-1.6.0.0-1.7.b09.el5
```

## 安装JDK

Oracle官网下载JDK，我下载的是  
jdk-11.0.13\_linux-x64\_bin.tar.gz,注意这个是tar.gz版本的JDK，这个版本的JDK解压、配置环境变量即可，不需要安装。

-   解压jdk到/usr/local/目录

```
tar -zxvf jdk-11.0.13_linux-x64_bin.tar.gz  -C  /usr/local/
```

-   /etc/profile配置环境变量

在/etc/profile 文件末尾增加如下配置：

```
export JAVA_HOME=/usr/local/jdk-11.0.13;
export CLASSPATH=.:${JAVA_HOME}/lib:$CLASSPATH;
export PATH=${JAVA_HOME}/bin:$PATH;
```

通过命令source /etc/profile让配置生效，然后java -version测试一下安装结果。

![CentOS7安装JDK11保姆级教程](https://p3.toutiaoimg.com/origin/tos-cn-i-qvj2lq49k0/2f0765b3fb224712bffb65a094cfce25?from=pc)
