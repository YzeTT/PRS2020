package executor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.baeldung.spring.data.redis.queue.RedisMessagePublisherTask;
import com.baeldung.spring.data.redis.queue.RedisMessageSubscriberAck;

@Slf4j
public class MyTask implements RunnableWithType {

    RedisMessagePublisherTask redisMessagePublisher;

    MessageListenerAdapter redisMessageListener;

    Type type;
    Long id;

    public MyTask(Long id, Type type, @Qualifier("publisherTask") RedisMessagePublisherTask redisMessagePublisher,
            @Qualifier("listenerAck")
                    MessageListenerAdapter redisMessageListener) {
        this.type = type;
        this.id = id;
        this.redisMessagePublisher = redisMessagePublisher;
        this.redisMessageListener = redisMessageListener;
    }

    @SneakyThrows @Override public void run() {
        redisMessagePublisher.publish(id);
        ((RedisMessageSubscriberAck) redisMessageListener.getDelegate()).waitOnStart(id);
        log.info("Task {} {}", id, type);
        redisMessagePublisher.publish(id);
        ((RedisMessageSubscriberAck) redisMessageListener.getDelegate()).waitOnEnd(id);
    }
    @Override public String getType() {
        return type.name();
    }
}

