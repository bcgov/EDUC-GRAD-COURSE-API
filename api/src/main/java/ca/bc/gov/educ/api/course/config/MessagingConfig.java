package ca.bc.gov.educ.api.course.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

@Configuration
public class MessagingConfig {

    @Bean
    @Qualifier("core-nats")
    public ExecutorService  coreNatsExecutor() {
        return new EnhancedQueueExecutor.Builder()
                .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("core-nats-%d").build())
                .setCorePoolSize(10)
                .setMaximumPoolSize(50)
                .setKeepAliveTime(Duration.ofSeconds(60))
                .build();
    }

    @Bean
    @Qualifier("nats-message-subscriber")
    public ExecutorService natsMessageSubscriberExecutor() {
        return new EnhancedQueueExecutor.Builder()
                .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("nats-message-subscriber-%d").build())
                .setCorePoolSize(10)
                .setMaximumPoolSize(10)
                .setKeepAliveTime(Duration.ofSeconds(60))
                .build();
    }
}
