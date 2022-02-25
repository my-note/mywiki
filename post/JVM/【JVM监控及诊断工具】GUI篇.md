# 1. 概述

- **JDK自带的工具**

	- **jconsole**：JDK自带的可视化监控工具。查看java应用程序的运行概况，监控堆信息，永久区（或元空间）使用情况、类加载情况等。
	- **Visual VM**：VM是一个工具，他提供了一个可视界面，用户查看java虚拟机上运行的基于java技术的应用程序的详细信息。
	- **JMC**：java mission Control,内置的java flight recorder.能够以极低的性能开销收集java虚拟机的性能数据。


- **第三方工具**
	
	- **MAT**：MAT（Memory Analyzer Tool）,基于eclipse的内存分析工具，heap分析工具，查找内存泄露（eclipse插件形式）
	- **JProfiler**：商业软件，需要付费，功能强大。
	- **Arthas**：alibaba开源的java诊断工具
	- **Btrace**：java运行时的追踪工具，可以在不停机的情况下，跟踪指定的方法调用，构造函数调用
	- **FlameGraphs**：(火焰图)

# 2. jconsole


- 自从JAVA5开始，在JDK中自带的java监控和管理控制台
- 用于对JVM中内存、线程和类等的监控，是一个基于JMX（java management extensions）的GUI性能监控工具。


# 3. Visual VM

- Visual VM是一个功能强大的多合一的故障诊断和性能监控的可视化工具
- 集成了多个JDK命令行工具，使用Visual VM用于显示虚拟机进程以及进程的配置和环境信息（jps、jinfo),监视应用程序的CPU、GC、堆、方法区以及线程的信息（jstat、jstack)等，代替jconsole
- visual vm作为jdk的一部分发布，完全免费，也可以作为独立的软件安装。[https://visualvm.github.io/index.html](https://visualvm.github.io/index.html)
- 支持插件，可以离线下载插件文件`*.nbm`,也可以在线安装。<font color="red">(推荐安装Visual GC)</font> 插件地址[https://visualvm.github.io/pluginscenters.html](https://visualvm.github.io/pluginscenters.html)
- idea支持visual VM插件启动`VisualVM Launcher`
- 可以分析heap dump文件


> 连接远程springboot?
>> -Dcom.sun.management.jmxremote
>> -Dcom.sun.management.jmxremote.port=1099
>> -Dcom.sun.management.jmxremote.rmi.port=1099
>> -Dcom.sun.management.jmxremote.authenticate=false
>> -Dcom.sun.management.jmxremote.ssl=false
>> -Djava.rmi.server.hostname=<ip地址，注意是公网ip>



 # 4. mat

 eclipse插件 分析dump文件

 
# 5. JProfiler

- IDEA插件
- 收费
- [官网](https://www.ej-technologies.com/products/jprofiler/overview.html)


<big>**特点：**</big>

1. 使用方便，界面操作友好（简单且强大）
2. 对被分析的应用影响小（提供模板）
3. CPU、Thread、Memory分析功能尤其强大
4. 支持对jdbc、nosql、jsp、servlet等进行分析
5. 支持多种模式（离线、在线）分析
6. 支持监控本地、远程JVM
7. 跨平台，拥有多钟操作系统的安装版本

<big>**数据采集方式：**</big>

1. Instrumentation: 全功能模式，在class加载前，JProfiler把相关功能代码写入到需要分析的class的bytecode(字节码文件)中，对正在运行的jvm有一定的影响。

	- 优点：功能强大，调用堆栈信息准确
	- 缺点：对应用的性能影响较大，cpu开销高。（一般配合Filter使用，对特定的类或包进行分析）

2. Sampling: 采样分析

	- 优点：cpu开销低，对应用影响小
	- 缺点：一些数据、特性不能提供


# 6. Arthas(阿尔萨斯)

































































































































