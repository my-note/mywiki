# Disruptor学习笔记之个人总结


1. 为什么叫BatchEventProcessor?

  - 因为消费者消费Event是一批一批消费的，当有顺序关系的消费者时，前一个消费者消费完一批event后，后一个消费者(依赖前一个消费者)才开始消费这一批（同一批）event.当所有的消费者消费完event后，event才会被覆盖重写。
  - endOfBatch => 更新Sequence当前值：set sequence => 后面的消费者依赖前面消费的的sequence(生产者依赖所有消费者中sequence最小的值)

2. 怎么划定一批数据呢？Batch的界限在哪里？endOfBatch is true?



  - 当没有元素的时候，也就是下一个solt是空的时候（没有生产者生成，消费完成）就是一批的结束，endOfBatch: true => producer sequence
  - ringBufferSize 数组索引位有一个序号栅栏
  - 依赖的上一个消费者的sequence值的时候
  - 当到达手动指定（拨钟）的数组的索引位置的时候

3. 有两个重要的参数调节性能：
  - ringBufferSize：环的缓存大小, 吞吐量缓存能力
  - batchRemaining: 处理大批量切分成小批量, 处理速率,低延迟



















