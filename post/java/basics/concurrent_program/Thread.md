# Thread

## 1. 创建、启动线程

![10006](img/10006.png)


## 2. 停止线程

![10007](img/10007.png)

![10008](img/10008.png)

![10009](img/10009.png)

### 2.1 run方法内没有sleep或wait方法时，停止线程

```java

public class A implements Runnable {
    @Override
    public void run() {

        int num = 0;
        while (!Thread.currentThread().isInterrupted() && num <= Integer.MAX_VALUE / 2){
            if ( num % 10000 == 0){
                System.out.println(num + "是10000的倍数");
            }
            num++;
        }

        System.out.println("任务运行结束");

    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new A());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }
}

```

### 2.2 run方法内带有sleep中断线程的写法

需要try-catch处理，通过代码手动决定是否中断、何时中断

```java

/**
 * run方法内带有sleep中断线程的写法
 */
public class B  {


    public static void main(String[] args) throws InterruptedException {

        Runnable runnable = () -> {
            try {
                int num = 0;
                while (num <= 300 && !Thread.currentThread().isInterrupted()){
                    if (num % 100 == 0){
                        System.out.println(num + "是100倍数");
                    }
                    num++;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };

        Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(500);
        thread.interrupt();


    }
}

```
### 2.3 错误停止线程的方法

![10011](img/10011.png)

## 3. 响应中断方法列表

响应中断方法：如果一个中断信号过来了，方法能够感知到interrupt中断信号


* Object.wait()/wait(long)/wait(long, int)
* Thread.sleep(long)/sleep(long, int)
* Thread.join()/join(long)/join(long, int)
* java.util.concurrent.BlockingQueue.take()/put(E)
* java.util.concurrent.locks.Lock.lockInterruptibly()
* java.util.concurrent.CountDownLatch.await()
* java.util.concurrent.CyclicBarrier.await()
* java.util.concurrent.Exchanger.exchange(V)
* java.nio.channels.InterruptibleChannel相关方法
* java.nio.channels.Selector的相关方法

## 4. 线程的生命周期（6状态）

![10012](img/10012.png)

![10013](img/10013.png)


















































