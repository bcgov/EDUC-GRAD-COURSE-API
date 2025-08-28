package ca.bc.gov.educ.api.course.messaging.jetstream;

import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.service.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.JsonUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.DeliverPolicy;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static ca.bc.gov.educ.api.course.constants.Topics.COREG_EVENTS_TOPIC;
import static ca.bc.gov.educ.api.course.util.EducCourseApiConstants.COREG_EVENTS_TOPIC_DURABLE;

/**
 * The type Subscriber.
 */
@Component
@DependsOn("publisher")
@Slf4j
public class Subscriber {
    private final Executor subscriberExecutor;
    private final EventHandlerDelegatorService eventHandlerDelegatorService;

    private final Connection natsConnection;
    private final Map<String, List<String>> streamTopicsMap = new HashMap<>(); // one stream can have multiple topics.
    private final EducCourseApiConstants constants;

    private Dispatcher dispatcher;

    /**
     * Instantiates a new Subscriber.
     *
     * @param natsConnection               the nats connection
     * @param eventHandlerDelegatorService
     * @param constants
     */
    @Autowired
    public Subscriber(final Connection natsConnection,
                      EventHandlerDelegatorService eventHandlerDelegatorService,
                      EducCourseApiConstants constants) {
        this.eventHandlerDelegatorService = eventHandlerDelegatorService;
        this.natsConnection = natsConnection;
        this.constants = constants;
        this.subscriberExecutor = new EnhancedQueueExecutor.Builder()
                .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("jet-stream-subscriber-%d").build())
                .setCorePoolSize(10).setMaximumPoolSize(10).setKeepAliveTime(Duration.ofSeconds(60)).build();
        this.initializeStreamTopicMap();
    }

    private void initializeStreamTopicMap() {
        final List<String> coregEventsTopics = new ArrayList<>();
        coregEventsTopics.add(COREG_EVENTS_TOPIC.name());
        this.streamTopicsMap.put(EducCourseApiConstants.COREG_STREAM_NAME, coregEventsTopics);
    }

    /**
     * This subscription will make sure the messages are required to acknowledge manually to Jet Stream.
     * Subscribe.
     *
     * @throws IOException the io exception
     */


    @PostConstruct
    public void subscribe() throws IOException, JetStreamApiException {
        log.info("Subscribing to streams: {}", streamTopicsMap);
        dispatcher = this.natsConnection.createDispatcher();
        val qName = EducCourseApiConstants.API_NAME.concat("-QUEUE");
        val autoAck = false;

        for (val entry : this.streamTopicsMap.entrySet()) {
            for (val topic : entry.getValue()) {
                // Each consumer should have a durable name so JetStream tracks ack state
                final PushSubscribeOptions options = PushSubscribeOptions.builder()
                        .stream(entry.getKey())
                        .durable(COREG_EVENTS_TOPIC_DURABLE) // consumer state persists
                        .configuration(ConsumerConfiguration.builder()
                                .deliverPolicy(DeliverPolicy.New) // only new messages
                                .build())
                        .build();

                // Queue group + durable
                natsConnection.jetStream().subscribe(
                        topic,
                        qName,
                        dispatcher,
                        this::onMessage,
                        autoAck,
                        options
                );

                log.info("Subscription established: stream={}, topic={}, queue={}, durable={}",
                        entry.getKey(), topic, qName, COREG_EVENTS_TOPIC_DURABLE);
            }
        }
    }

    /**
     * This method will process the event message pushed into different topics of different APIS.
     * All APIs publish ChoreographedEvent
     *
     * @param message the string representation of {@link ChoreographedEvent} if it not type of event then it will throw exception and will be ignored.
     */
    public void onMessage(final Message message) {
        log.debug("OnMessage Invoked");
        if (message == null) return;
        log.debug("Received message subject={} seq={}", message.getSubject(), message.metaData().consumerSequence());
        try {
            val eventString = new String(message.getData());
            final ChoreographedEvent event = JsonUtil.getJsonObjectFromString(ChoreographedEvent.class, eventString);

            if (event.getEventPayload() == null) {
                message.ack();
                log.warn("Ignoring event with null payload :: {}", event);
                return;
            }

            subscriberExecutor.execute(() -> {
                try {
                    eventHandlerDelegatorService.handleChoreographyEvent(event, message);
                    message.ack(); // ✅ always ack after processing
                } catch (Exception e) {
                    log.error("Error processing message", e);
                }
            });
        } catch (Exception ex) {
            log.error("Exception parsing message", ex);
        }
    }
}
