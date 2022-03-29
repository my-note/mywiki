# Disruptor学习笔记之Batch





## Batch Rewind

当使用BatchEventProcessor来批量处理事件时，有一个可用的特性可以用来从一个名为“Batch Rewind”的异常中恢复。如果在处理可恢复的事件时出现错误，用户可以抛出RewindableException。这将调用BatchRewindStrategy而不是通常的ExceptionHandler来决定Sequence是应该回退到要重新尝试的批处理的开始，还是应该重新抛出，并委托给ExceptionHandler。

e.g.

当使用SimpleBatchRewindStrategy(它将总是rewind)，然后BatchEventProcessor从150-155接收一个批处理，但一个临时的失败发生在序列153(它抛出一个RewindableException)。被处理的事件如下所示

    150, 151, 152, 153(failed -> rewind), 150, 151, 152, 153(succeeded this time), 154, 155

默认的BatchRewindStrategy是SimpleBatchRewindStrategy，但是不同的策略可以像这样提供给BatchEventProcessor

    batchEventProcessor.setRewindStrategy(batchRewindStrategy);

[https://lmax-exchange.github.io/disruptor/user-guide/index.html#_use_case](https://lmax-exchange.github.io/disruptor/user-guide/index.html#_use_case) 

### Use Case

Happy case

    Batch start -> START TRANSACTION;
    Event 1 ->     insert a row;
    Event 2 ->     insert a row;
    Event 3 ->     insert a row;
    Batch end ->   COMMIT;


Sad case without Batch Rewind

    Batch start -> START TRANSACTION;
    Event 1 ->     insert a row;
    Event 2 ->     DATABASE has a blip and can not commit
    Throw error -> ROLLBACK;
    User needs to explcitily reattempt the batch or choose to abandon the batch

Sad case with Batch Rewind

    Batch start ->               START TRANSACTION;
    Event 1 ->                   insert a row;
    Event 2 ->                   DATABASE has a blip and can not insert
    Throw RewindableException -> ROLLBACK;
    Batch start ->               START TRANSACTION;
    Event 1 ->                   insert a row;
    Event 2 ->                   insert a row;
    Event 3 ->                   insert a row;
    Batch end ->                 COMMIT;

















































