

```
-server 
-Xms1g 
-Xmx1g 
-Xss256k 
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=./heapdump.hprof 
-XX:+PrintGCDetails 
-XX:+PrintGCDateStamps 
-XX:+PrintGCTimeStamps 
-Xloggc:./gc.log 
-XX:NumberOfGClogFiles=3
-XX:GCLogFileSize=8192
-XX:+UseG1GC
```



> Java8：默认Parallel Scavenge + Parallel Old组合
> Java9、10：默认G1收集器














