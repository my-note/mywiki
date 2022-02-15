# JUC并发工具类


* [并发工具类-分类](./img/并发工具类-分类.png)
* [线程池](线程池.md)
* [获取线程的执行结果](获取线程的执行结果.md)
* [锁工具](lock.md)
* 并发集合
    * [ConcurrentHashMap：线程安全的HashMap](ConcurrentHashMap线程安全的HashMap.md)
    * [CopyOnWriteArrayList: 线程安全的List](CopyOnWriteArrayList线程安全的List.md)
    * [BlockingQueue：这是一个接口,表示阻塞队列,非常适合用于作为数据共享的通道](BlockingQueue.md)
    * [ConcurrentLinkedQueue: 高效的非组赛的并发队列，使用链表实现。可以看做是一个线程安全的LinkedList](ConcurrentLinkedQueue.md)
    * [ConcurrentSkipListMap: 是一个Map,使用跳表的数据结构进行快速查找](ConcurrentSkipListMap.md)
* 控制并发流程
    * [CountDownLatch倒计时门闩](CountDownLatch倒计时门闩.md)
    * [Semaphore信号量](Semaphore信号量.md)
    * [Condition接口(又称条件对象)](Condition接口.md)
    * [CyclicBarrier循环栅栏](CyclicBarrier循环栅栏.md)


| 类               | 作用                                                                                       | 说明                                                               |
| ---------------- | ---------------                                                                            | ---------------                                                    |
| Semaphore        | 信号量，可以通过控制“许可证”的数量，来保证线程之间的配合                                   | 线程只有在拿到“许可证”以后才能继续运行，相比于其他的同步器，更灵活 |
| CyclicBarrier    | 线程会等待，直到足够多的线程达到了事先规定的数目。一旦达到了触发条件，就可以进行下一步动作 | 使用于线程之间相互等待处理结果就绪的场景                           |
| Phaser           | 和CyclicBarrier类似，但是计数可变                                                          | Java7加入                                                          |
| CountDownLatch   | 和CyclicBarrier类似，数量递减到0时，触发动作                                               | 不可重复使用                                                       |
| Exchanger        | 让两个线程在合适时交换对象                                                                 | 使用场景：当两个线程工作在同一个类的不同实例上时，用于交换数据     |
| Condition        | 可以控制线程的“等待”和“唤醒”                                                               | 是Object.wait()的升级版                                            |


* [AQS](AQS.md)















































