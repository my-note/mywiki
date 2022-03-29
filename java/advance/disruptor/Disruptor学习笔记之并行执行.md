# Disruptor学习笔记之并行执行


```java
import com.example.disruptor.event.OrderEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 消费者
 * @author user
 */
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    private Sequence sequenceCallback;


    @Override
    public void setSequenceCallback(final Sequence sequenceCallback) {
        this.sequenceCallback = sequenceCallback;
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("start event:{}, sequence:{}, endOfBatch:{}", event, sequence, endOfBatch);
        TimeUnit.MILLISECONDS.sleep(3000);
        log.info("end event:{}, sequence:{}, endOfBatch:{}", event, sequence, endOfBatch);
        this.sequenceCallback.set(sequence);
    }
}
```






```java
import com.example.disruptor.event.OrderEvent;
import com.example.disruptor.handler.OrderEventHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author user
 */
@Service
@Slf4j
public class OrderEventProducer {


    private final RingBuffer<OrderEvent>  ringBuffer;

    private static final EventTranslatorOneArg<OrderEvent, Long> TRANSLATOR = (event, sequence, id) -> event.setId(id);


    public OrderEventProducer(){
        //定义RingBuffer大小，必须是2的幂次方 e.g. 1024 * 1024
        int ringBufferSize = 16;
        //构造一个disruptor实例
        Disruptor<OrderEvent> disruptor = new Disruptor<>(OrderEvent::new,
                ringBufferSize,
                new NamedThreadFactory("order-event-"),
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        //绑定handler消费者
        OrderEventHandler handler1 = new OrderEventHandler();
        OrderEventHandler handler2 = new OrderEventHandler();

        disruptor.handleEventsWith(handler1, handler2);






        //启动
        this.ringBuffer = disruptor.start();

    }

    public void onData(Long orderId) {

        ringBuffer.publishEvent(TRANSLATOR, orderId);
        log.info("====> publish id : {}", orderId);
    }

}
```


方式一：

    disruptor.handleEventsWith(handler1);
    disruptor.handleEventsWith(handler2);

方式二：

    disruptor.handleEventsWith(handler1, handler2);














