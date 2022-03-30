# Disruptor学习笔记之多生产者模型

生产者有两种模式：单生产者 & 多生产者

```java
public enum ProducerType
{
    /**
     * Create a RingBuffer with a single event publisher to the RingBuffer
     */
    SINGLE,

    /**
     * Create a RingBuffer supporting multiple event publishers to the one RingBuffer
     */
    MULTI
}
```

在构造Disruptor实例的时候，如果不指定默认使用多生产者模式，也可以直接指定生产者模式，两种构造函数：


* Disruptor(eventFactory, ringBufferSize, threadFactory) : 不指定默认使用多生产者模式
* Disruptor(eventFactory, ringBufferSize, threadFactory, producerType, waitStrategy) : 直接指定生产者模式

```java
//默认使用多生产者模式
public Disruptor(final EventFactory<T> eventFactory, final int ringBufferSize, final ThreadFactory threadFactory) {
        this(RingBuffer.createMultiProducer(eventFactory, ringBufferSize), threadFactory);
}
//也可以直接指定生产者模式
public Disruptor( final EventFactory<T> eventFactory, final int ringBufferSize, final ThreadFactory threadFactory, final ProducerType producerType, final WaitStrategy waitStrategy) {
        this( RingBuffer.create(producerType, eventFactory, ringBufferSize, waitStrategy), threadFactory);
}
```

> 1. 在并发系统中提高性能的最好方法之一是坚持Single Writer原则，这适用于Disruptor。如果你处在只有一个线程产生事件到Disruptor的情况下，那么你可以利用这一点来获得额外的性能
> 2. 如果有多个生产者情况下，要选择多个生产者，多发生产数据序号递增有并发安全，说白了多生产者牺牲了一部分性能提高了安全性






















