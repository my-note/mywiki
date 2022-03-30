# Disruptor学习笔记之3对比4


1. 3.X.X 有多消费者模型；4.X.X没有多消费者模型
2. 4.X.X 的EventHandler 多了很多监听事件

```java

public interface EventHandler<T>
{
    void onEvent(T event, long sequence, boolean endOfBatch) throws Exception;

    default void onBatchStart(long batchSize) {
    }

    default void onStart() {
    }

    default void onShutdown() {
    }

    default void setSequenceCallback(Sequence sequenceCallback) {
    }

    default void onTimeout(long sequence) throws Exception {
    }
}

```







































