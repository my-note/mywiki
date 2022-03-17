![](http://placekitten.com/1200/360)


## uptime

* uptime: 查看机器负载的情况(cpu)

```shell
$ uptime
09:43:13 up  1:41,  1 user,  load average: 0.00, 0.03, 0.05
# ---------------------------------------- imin  5min  15min
```

## mpstat

* mpstat: 查看每个cpu的使用情况（可以查看到cpu个数）

```shell

$ mpstat -P ALL 1
Linux 3.10.0-1160.31.1.el7.x86_64 (eink-predev-01) 	2022年01月25日 	_x86_64_(4 CPU)

09时45分19秒  CPU    %usr   %nice    %sys %iowait    %irq   %soft  %steal  %guest  %gnice   %idle
09时45分20秒  all    0.75    0.00    1.00    0.00    0.00    0.00    0.00    0.00    0.00   98.25
09时45分20秒    0    1.98    0.00    0.99    0.00    0.00    0.00    0.00    0.00    0.00   97.03
09时45分20秒    1    0.99    0.00    1.98    0.00    0.00    0.00    0.00    0.00    0.00   97.03
09时45分20秒    2    1.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00   99.00
09时45分20秒    3    0.00    0.00    1.01    0.00    0.00    0.00    0.00    0.00    0.00   98.99
```


## pidstat

* pidstat: 进程的cpu占用率

```shell
# 间隔时间：5s   运行次数：3次
$ pidstat -p pid 5 3

```

## lsof

* lsof: 通过端口号找进程

```shell

$ sudo lsof -i:8080

```

## free

* free: 内存使用情况

```shell

# -h 以人类可读的方式显示
$ free -h

# -m 以M为单位显示
$ free -m

```

## iostat

* iostat: 机器的磁盘io情况

































































