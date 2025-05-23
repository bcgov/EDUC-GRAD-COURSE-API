package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.messaging.MessagePublisher;
import ca.bc.gov.educ.api.course.struct.Event;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static ca.bc.gov.educ.api.course.service.EventHandlerService.PAYLOAD_LOG;


/**
 * The type Event handler service.
 */
@Service
@Slf4j
@SuppressWarnings({"java:S3864", "java:S3776"})
public class EventHandlerDelegatorService {


    /**
     * The constant RESPONDING_BACK_TO_NATS_ON_CHANNEL.
     */
    public static final String RESPONDING_BACK_TO_NATS_ON_CHANNEL = "responding back to NATS on {} channel ";
    private final MessagePublisher messagePublisher;
    private final EventHandlerService eventHandlerService;

    /**
     * Instantiates a new Event handler delegator service.
     *
     * @param messagePublisher    the message publisher
     * @param eventHandlerService the event handler service
     */
    @Autowired
    public EventHandlerDelegatorService(MessagePublisher messagePublisher, EventHandlerService eventHandlerService) {
        this.messagePublisher = messagePublisher;
        this.eventHandlerService = eventHandlerService;
    }

    /**
     * Handle event.
     *
     * @param event   the event
     * @param message the message
     */
    public void handleEvent(final Event event, final Message message) {
        byte[] response;
        boolean isSynchronous = message.getReplyTo() != null;
        try {
            if (Objects.requireNonNull(event.getEventType()) == EventType.GET_STUDENT_COURSE) {
                log.debug("received GET_STUDENT_COURSE event :: {}", event.getSagaId());
                log.trace(PAYLOAD_LOG, event.getEventPayload());
                response = eventHandlerService.handleGetStudentCourseEvent(event);
                log.debug(RESPONDING_BACK_TO_NATS_ON_CHANNEL, message.getReplyTo() != null ? message.getReplyTo() : event.getReplyTo());
                publishToNATS(event, message, isSynchronous, response);
            } else {
                log.info("silently ignoring other events :: {}", event);
            }
        } catch (final Exception e) {
            log.error("Exception", e);
        }
    }

    private void publishToNATS(Event event, Message message, boolean isSynchronous, byte[] left) {
        if (isSynchronous) { // sync, req/reply pattern of nats
            messagePublisher.dispatchMessage(message.getReplyTo(), left);
        } else { // async, pub/sub
            messagePublisher.dispatchMessage(event.getReplyTo(), left);
        }
    }
}
