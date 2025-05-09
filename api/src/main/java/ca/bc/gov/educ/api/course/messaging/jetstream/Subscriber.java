package ca.bc.gov.educ.api.course.messaging.jetstream;

import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.service.JetStreamEventHandlerService;
import ca.bc.gov.educ.api.course.struct.Event;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.DeliverPolicy;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

import static ca.bc.gov.educ.api.course.constants.Topics.GRAD_COURSE_EVENTS_TOPIC;
import static ca.bc.gov.educ.api.course.util.EducCourseApiConstants.EVENTS_TOPIC_DURABLE;

/**
 * The type Subscriber.
 */
@Component
@DependsOn("publisher")
@Slf4j
public class Subscriber {
    private final JetStreamEventHandlerService jetStreamEventHandlerService;
    private final Connection natsConnection;

    /**
     * Instantiates a new Subscriber.
     *
     * @param natsConnection          the nats connection
     * @param jetStreamEventHandlerService the stan event handler service
     */
    @Autowired
    public Subscriber(final Connection natsConnection, final JetStreamEventHandlerService jetStreamEventHandlerService) {
        this.jetStreamEventHandlerService = jetStreamEventHandlerService;
        this.natsConnection = natsConnection;
    }


    /**
     * This subscription will make sure the messages are required to acknowledge manually to Jet Stream.
     * Subscribe.
     *
     * @throws IOException the io exception
     */
    @PostConstruct
    public void subscribe() throws IOException, JetStreamApiException {
        log.debug("Attempting to subscribe to GRAD_COURSE_EVENTS_TOPIC...");
        val autoAck = false;
        PushSubscribeOptions options = PushSubscribeOptions.builder().stream(EducCourseApiConstants.STREAM_NAME)
                .durable(EVENTS_TOPIC_DURABLE)
                .configuration(ConsumerConfiguration.builder().deliverPolicy(DeliverPolicy.New).build()).build();
        this.natsConnection.jetStream().subscribe(GRAD_COURSE_EVENTS_TOPIC.toString(), EVENTS_TOPIC_DURABLE, this.natsConnection.createDispatcher(), this::onGradCourseEventsTopicMessage,
                autoAck, options);

        log.debug("Subscription successfully established for GRAD_COURSE_EVENTS_TOPIC.");
    }

    /**
     * This method will process the event message pushed into the grad_course_events_topic.
     * this will get the message and update the event status to mark that the event reached the message broker.
     * On message message handler.
     *
     * @param message the string representation of {@link Event} if it not type of event then it will throw exception and will be ignored.
     */
    public void onGradCourseEventsTopicMessage(final Message message) {
        log.debug("Received message Subject:: {} , SID :: {} , sequence :: {}, pending :: {} ", message.getSubject(), message.getSID(), message.metaData().consumerSequence(), message.metaData().pendingCount());
        try {
            val eventString = new String(message.getData());
            ca.bc.gov.educ.api.course.util.LogHelper.logMessagingEventDetails(eventString);
            ChoreographedEvent event = JsonUtil.getJsonObjectFromString(ChoreographedEvent.class, eventString);
            log.debug("Received event: eventType = {}, eventPayload = {}", event.getEventType(), event.getEventPayload());
            jetStreamEventHandlerService.updateEventStatus(event);
            log.debug("received event :: {} ", event);
            message.ack();
        } catch (final Exception ex) {
            log.error("Exception ", ex);
        }
    }

}
