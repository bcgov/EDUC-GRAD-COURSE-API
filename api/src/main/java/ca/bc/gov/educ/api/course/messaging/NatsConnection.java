package ca.bc.gov.educ.api.course.messaging;

import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;

/**
 * The type Nats connection.
 */
@Component
@Slf4j
public class NatsConnection implements Closeable {

    private final Connection natsCon;

    /**
     * Instantiates a new Nats connection.
     *
     * @param applicationProperties the application properties
     * @param coreNatsExecutor      the injected Executor from MessagingConfig (qualified as "core-nats")
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Autowired
    public NatsConnection(final EducCourseApiConstants applicationProperties, @Qualifier("core-nats") ExecutorService coreNatsExecutor) throws IOException, InterruptedException {
        this.natsCon = connectToNats(applicationProperties.getNatsUrl(), applicationProperties.getNatsMaxReconnect(), coreNatsExecutor);
    }

    private Connection connectToNats(String stanUrl, int maxReconnects, ExecutorService executor) throws IOException, InterruptedException {
        io.nats.client.Options natsOptions = new io.nats.client.Options.Builder()
                .connectionListener(this::connectionListener)
                .maxPingsOut(5)
                .pingInterval(Duration.ofSeconds(2))
                .connectionName("COREG-API")
                .connectionTimeout(Duration.ofSeconds(5))
                .executor(executor) // use the injected executor here
                .maxReconnects(maxReconnects)
                .reconnectWait(Duration.ofSeconds(2))
                .servers(new String[]{stanUrl})
                .build();
        return Nats.connect(natsOptions);
    }

    private void connectionListener(Connection connection, ConnectionListener.Events events) {
        log.info("NATS -> {}", events.toString());
    }

    @Override
    public void close() {
        if (natsCon != null) {
            log.info("closing nats connection...");
            try {
                natsCon.close();
            } catch (InterruptedException e) {
                log.error("error while closing nats connection...", e);
                Thread.currentThread().interrupt();
            }
            log.info("nats connection closed...");
        }
    }

    @Bean
    public Connection connection() {
        return natsCon;
    }
}
