package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.choreographer.ChoreographEventHandler;
import ca.bc.gov.educ.api.course.constants.ActivityCode;
import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.constants.Topics;
import ca.bc.gov.educ.api.course.messaging.MessagePublisher;
import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.model.dto.Event;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static ca.bc.gov.educ.api.course.service.EventHandlerService.PAYLOAD_LOG;


/**
 * The type Event handler service.
 */
@Service
@Slf4j
@SuppressWarnings({"java:S3864", "java:S3776"})
public class EventHandlerDelegatorService {

    private final ChoreographedEventPersistenceService choreographedEventPersistenceService;
    private final ChoreographEventHandler choreographer;
    private final EventHandlerService eventHandlerService;

    /**
     * The constant RESPONDING_BACK_TO_NATS_ON_CHANNEL.
     */
    public static final String RESPONDING_BACK_TO_NATS_ON_CHANNEL = "responding back to NATS on {} channel ";
    private final MessagePublisher messagePublisher;


    /**
     * Instantiates a new EventEntity handler delegator service.
     *
     * @param choreographedEventPersistenceService the choreographed event persistence service
     * @param choreographer                        the choreographer
     * @param eventHandlerService
     * @param messagePublisher
     */
    @Autowired
    public EventHandlerDelegatorService(final ChoreographedEventPersistenceService choreographedEventPersistenceService, final ChoreographEventHandler choreographer, EventHandlerService eventHandlerService, MessagePublisher messagePublisher) {
        this.choreographedEventPersistenceService = choreographedEventPersistenceService;
        this.choreographer = choreographer;
        this.eventHandlerService = eventHandlerService;
        this.messagePublisher = messagePublisher;
    }

    /**
     * this method will do the following.
     * 1. Call service to store the event in oracle DB.
     * 2. Acknowledge to STAN only when the service call is completed. since it uses manual acknowledgement.
     * 3. Hand off the task to update RDB onto a different executor.
     *
     * @param choreographedEvent the choreographed event
     * @param message            the message
     * @throws IOException the io exception
     */
    public void handleChoreographyEvent(@NonNull ChoreographedEvent choreographedEvent, final Message message) throws IOException {
        // some messages come in already with an activity code, some do not.
        // set the activity code early in the process
        setActivityCode(choreographedEvent, message);

        if(!this.choreographedEventPersistenceService.eventExistsInDB(choreographedEvent).isPresent()) {
            final var persistedEvent = this.choreographedEventPersistenceService.persistEventToDB(choreographedEvent);
            if(persistedEvent != null) {
                message.ack(); // acknowledge to Jet Stream that api got the message and it is now in DB.
                log.debug("acknowledged to Jet Stream for EVENT received: {}", persistedEvent.getEventType());
                this.choreographer.handleEvent(persistedEvent);
            }
        } else {
            message.ack(); // acknowledge to Jet Stream that api got the message and it is already in DB.
            log.debug("Event with ID {} already exists in the database. No further action taken.", choreographedEvent.getEventID());
        }
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
            if (Objects.requireNonNull(event.getEventType()).compareTo(EventType.GET_STUDENT_COURSE.name()) == 0) {
                log.debug("received GET_STUDENT_COURSE event");
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

    /**
     * Applies the correct activity code to ChoreographedEvents.
     * Add new activity codes here
     * @param choreographedEvent the choreographed event object
     * @param message message received from nats
     */
    private void setActivityCode(@NonNull final ChoreographedEvent choreographedEvent, final Message message) {
        Topics topics;
        try {
            topics = Topics.valueOf(message.getSubject());
            switch (topics) {
                case COREG_EVENTS_TOPIC:
                    choreographedEvent.setActivityCode(ActivityCode.COREG_EVENT.name());
                    break;
                default: // do nothing
                    break;
            }
        } catch (Exception e) {
            log.error("{} is not a valid topic", message.getSubject());
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
