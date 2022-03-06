## 一、背景

相信大家肯定遇到过我开头提到过的几种问题吧。也相信各位一定写过如下重复无意义的计时代码吧。当一段代码耗时极长，并且调用接口众多时，我们就不得不去分步统计看到底是哪个接口拖后腿，并以此定位接口性能瓶颈在哪里。那有如下代码就不奇怪了，我常常在想，是否有一套工具来帮我们统计接口耗时、占比，帮我们分析慢接口、慢调用呢？直到我遇到了他——StopWatch！

```java
public static void func1() throws InterruptedException {
    long start = System.currentTimeMillis();
    System.out.println("phase1 do something....");
    Thread.sleep(1000);
    long phase1 = System.currentTimeMillis();
    System.out.printf("phase1 cost time %d ms\n", (phase1 - start));
    System.out.println("phase2 do something....");
    Thread.sleep(2000);
    long phase2 = System.currentTimeMillis();
    System.out.printf("phase2 cost time %d ms\n", (phase2 - phase1));
    System.out.println("phase3 do something....");
    Thread.sleep(3000);
    long phase3 = System.currentTimeMillis();
    System.out.printf("phase3 cost time %d ms\n", (phase3 - phase2));
    System.out.println("phase4 do something....");
    Thread.sleep(4000);
    long phase4 = System.currentTimeMillis();
    System.out.printf("phase4 cost time %d ms\n", (phase4 - phase3));
    long end = System.currentTimeMillis();
    System.out.printf("func1 cost %d ms\n", (end - start));
}
```

## 二、初遇

初遇 StopWatch 是同事跟我讲，说有个东西可以替代你这里的 end - start 代码。我抱着不屑一顾的态度去看了一眼他的代码，看到了 StopWatch ，追进源码大致一看，这归根结底不还是用了 System.nanoTime 去减了一下，跟我的有什么区别呀。当然，如果故事终结于此，也就不会有这篇博客了。让我决定深追下去的只有一个原因，这个东西是 Spring 家的，而且 Spring 用了他去做接口时间的统计如图所示：

由于我个人对 Spring 的盲目崇拜，觉得他用啥都是好的！我决定好好研究一下这个 StopWatch，事实证明，真香！

## 三、深究

### 3.1 使用

对一切事物的认知，都是从使用开始，那就先来看看它的用法。开头一段的代码，在替换成 StopWatch 后，会如下所示：

```java
public static void func2() throws InterruptedException {
    StopWatch stopWatch = new StopWatch("func2");
    stopWatch.start("phase1");
    System.out.println("phase1 do something....");
    Thread.sleep(1000);
    stopWatch.stop();
    System.out.printf("phase1 cost time %d ms\n", stopWatch.getLastTaskTimeMillis());
    stopWatch.start("phase2");
    System.out.println("phase2 do something....");
    Thread.sleep(2000);
    stopWatch.stop();
    System.out.printf("phase2 cost time %d ms\n", stopWatch.getLastTaskTimeMillis());
    stopWatch.start("phase3");
    System.out.println("phase3 do something....");
    Thread.sleep(3000);
    stopWatch.stop();
    System.out.printf("phase3 cost time %d ms\n", stopWatch.getLastTaskTimeMillis());
    stopWatch.start("phase4");
    System.out.println("phase4 do something....");
    Thread.sleep(4000);
    stopWatch.stop();
    System.out.printf("phase4 cost time %d ms\n", stopWatch.getLastTaskTimeMillis());
    System.out.printf("func1 cost %d ms\n", stopWatch.getTotalTimeMillis());
    System.out.println("stopWatch.prettyPrint() = " + stopWatch.prettyPrint());
}
```

乍一眼看上去，是不是觉得这代码反而比之前的还要多了。但是如果实际写起来，实际上是要比第一种好写许多的。只管控制每段代码统计的开始和结束即可，不用关心是哪个时间减哪个时间。执行结果更为喜人：

如图所示，StopWatch 不仅正确记录了上个任务的执行时间，并且在最后还可以给出精确的任务执行时间（纳秒级别）和耗时占比。其实并不止于此，StopWatch 还可以记录整个任务的走向流程，例如走过了哪几个任务，各个耗时都是可以通过方法拿到的。例如：

```java
System.out.println("stopWatch.getLastTaskName() = " + stopWatch.getLastTaskName());
System.out.println("stopWatch.getLastTaskInfo().getTimeMillis() = " + stopWatch.getLastTaskInfo().getTimeMillis());
Arrays.stream(stopWatch.getTaskInfo()).forEach(e->{
    System.out.println("e.getTaskName() = " + e.getTaskName());
    System.out.println("e.getTimeMillis() = " + e.getTimeMillis());
    System.out.println("---------------------------");
});
```

执行结果如下：

这个链路和信息目前看上去可能没什么用，在后面的扩展章节我会说明

### 3.2 源码

老规矩，由浅入深。看完用法，我们来看看源码。先看下组成 StopWatch 的属性

```java
/**
 * Identifier of this stop watch.
 * Handy when we have output from multiple stop watches
 * and need to distinguish between them in log or console output.
 * 本实例的唯一 Id，用于在日志或控制台输出时区分的。
 */
private final String id;
/**
 * 是否保持一个 taskList 链表
 * 每次停止计时时，会将当前任务放入这个链表，用以记录任务链路和计时分析
 */
private boolean keepTaskList = true;
/**
 * 任务链表
 */
private final List<TaskInfo> taskList = new LinkedList<>();

/** Start time of the current task. */
/** 当前任务的开始时间. */
private long startTimeNanos;

/** Name of the current task. */
/** 当前任务名称. */
@Nullable
private String currentTaskName;
@Nullable
/** 最后一个任务的信息. */
private TaskInfo lastTaskInfo;
/** 任务总数. */
private int taskCount;

/** Total running time. */
/** 总任务时间. */
private long totalTimeNanos;
```

StopWatch 内部持有一个内部类 TaskInfo，内有两个属性

```java
/**
 * Nested class to hold data about one task executed within the {@code StopWatch}.
 */
public static final class TaskInfo {
private final String taskName;
private final long timeNanos;
TaskInfo(String taskName, long timeNanos) {
this.taskName = taskName;
this.timeNanos = timeNanos;
}

public String getTaskName() { return this.taskName; }

public long getTimeNanos() { return this.timeNanos; }

public long getTimeMillis() { return nanosToMillis(this.timeNanos); }

public double getTimeSeconds() { return nanosToSeconds(this.timeNanos); }
}
```

这里要重点提一下，可能有的读者朋友看到的源码跟我这里贴出来的不一样。这是因为在 Spring 5.2 之前的 StopWatch 中统一使用的毫秒，即 TimeMillis 。而到 Spring 5.2 及其以后的版本，都统一改为纳秒即 TimeNanos 。

看完属性相信大家也差不多猜出来具体的实现了，不过就是维护了一个任务链表，然后开始的时候记一个时间，结束的时候记一个时间，最后取的时候减一下。此处由于篇幅原因就仅放 start 和 stop 两个方法的源码。大家如果对其余的方法感兴趣，请自行查阅源码（spring-core 模块  
org.springframework.util.StopWatch 类）。

```java

public void start(String taskName) throws IllegalStateException {
    if (this.currentTaskName != null) {
        throw new IllegalStateException("Can't start StopWatch: it's already running");
    }
    this.currentTaskName = taskName;

    this.startTimeNanos = System.nanoTime();
}

public void stop() throws IllegalStateException {
    if (this.currentTaskName == null) {
        throw new IllegalStateException("Can't stop StopWatch: it's not running");
    }

    long lastTime = System.nanoTime() - this.startTimeNanos;

    this.totalTimeNanos += lastTime;

    this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);

    if (this.keepTaskList) {
        this.taskList.add(this.lastTaskInfo);
    }
    ++this.taskCount;
    this.currentTaskName = null;
}
```

### 3.3 拓展

这里想聊一下 StopWatch 的用法，其实上述代码这种方法内定义一个，然后在同一个方法中使用是最基础的用法。下面给大家提供几个思路。（用法不仅限于此，大家可开动脑筋自行开发~）

在 Controller 层或 Service 层将 StopWatch 放入 ThreadLocal 当中。在整条 Service 调用链中，使用同一个 StopWatch 对象记录方法调用的耗时和路线。最后在结束时生成方法的耗时占比，以定位性能瓶颈。

利用 Spring-AOP 方式，在每个方法的开始和结束使用 StopWatch 记录接口耗时时间。

声明一个注解，利用 Spring-AOP 和注解的方式，自定义分析带注解的方法耗时。

在这几种情况下，3.1 章节中所说的任务调用链路就非常重要了。这个调用链路将是定位耗时的非常重要的手段。

## 四、总结

任何东西都有利害两面，最后我们来看下优点和缺点

* 优点：

  - 方便统计耗时，通过指定任务名称和耗时占比分析，可以清晰明确的定位到耗时慢的接口和调用。
  - 记录整条任务链路，方便全链路分析。
  - 仅需要关注代码耗时的起始点和终点，无需关注到底需要哪个时间减哪个时间

* 缺点：

  - 同一个 StopWatch 只能开启一个任务，无法统计 “包含” 类的耗时。例如 A 任务中包含 B 任务这种，只能再开启一个 StopWatch 实例
  - 无法中间插入一个任务，例如 先执行 A 任务，再执行 B 任务，再执行 A 任务。无法将 B 任务的执行时间单独隔离开。
