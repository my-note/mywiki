* 第一步：把jdk的压缩包上传到linux系统
 
* 第二步：解压缩jdk的包。


```bash
tar -zxvf jdk-7u55-linux-i586.tar.gz -C /usr/local/
```

 
* 第三步：把jdk1.7.0_55文件夹移动到/usr/local文件夹下。【跳过】
 
* 第四步：配置环境变量JAVA_HOME、PATH
 
	- 需要修改`/etc/profile`文件; 在文件中添加如下内容：


```bash
export JAVA_HOME=/usr/local/jdk1.8.0_74
export PATH=$JAVA_HOME/bin:$PATH
```

* 第五步：执行source命令

```bash
source /etc/profile
```

## yum 


```bash
yum search java-11
yum list | grep java-11
sudo yum -y install epel-release
sudo yum -y install java-11-openjdk
```

## sudo找不到命令

1. 用java全路径
2. java放到/usr/bin目录下
3. 修改/etc/sudoers文件
	- `Defaults    secure_path = /sbin:/bin:/usr/sbin:/usr/bin:/usr/local/jdk-11.0.6/bin`



























