package ca.bc.gov.educ.api.course.messaging;

import ca.bc.gov.educ.api.course.messaging.jetstream.Subscriber;
import ca.bc.gov.educ.api.course.service.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.course.struct.Event;
import ca.bc.gov.educ.api.course.util.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

import static ca.bc.gov.educ.api.course.constants.Topics.GRAD_COURSE_API_TOPIC;

/**
 * This is for subscribing directly to NATS. for subscribing to Jet Stream please
 * follow {@link Subscriber}
 */
@Component
@Slf4j
@Profile("!test")
public class MessageSubscriber {

    private final ExecutorService messageProcessingThreads;
    private final EventHandlerDelegatorService eventHandlerDelegatorServiceV1;
    private final Connection connection;

    @Autowired
    public MessageSubscriber(final Connection connection, final EventHandlerDelegatorService eventHandlerDelegatorServiceV1, @Qualifier("nats-message-subscriber") ExecutorService messageProcessingThreads) {
        this.eventHandlerDelegatorServiceV1 = eventHandlerDelegatorServiceV1;
        this.connection = connection;
        this.messageProcessingThreads = messageProcessingThreads;
    }

    /**
     * This subscription will makes sure the messages are required to acknowledge manually to STAN.
     * Subscribe.
     */
    @PostConstruct
    public void subscribe() {
        String queue = GRAD_COURSE_API_TOPIC.toString().replace("_", "-");
        var dispatcher = connection.createDispatcher(onMessage());
        dispatcher.subscribe(GRAD_COURSE_API_TOPIC.toString(), queue);
    }

    /**
     * On message message handler.
     *
     * @return the message handler
     */
    private MessageHandler onMessage() {
        return (Message message) -> {
            if (message != null) {
                try {
                    var eventString = new String(message.getData());
                    ca.bc.gov.educ.api.course.util.LogHelper.logMessagingEventDetails(eventString);
                    var event = JsonUtil.getJsonObjectFromString(Event.class, eventString);
                    if (event.getPayloadVersion() == null) {
                        event.setPayloadVersion("V1");
                    }
                    //placeholder to have different versions
                    if ("V1".equalsIgnoreCase(event.getPayloadVersion())) {
                        messageProcessingThreads.execute(() -> eventHandlerDelegatorServiceV1.handleEvent(event, message));
                    }
                } catch (final Exception e) {
                    log.error("Exception ", e);
                }
            }
        };
    }
}
