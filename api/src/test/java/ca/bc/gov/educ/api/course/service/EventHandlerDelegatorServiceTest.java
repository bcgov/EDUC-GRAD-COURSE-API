package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.choreographer.ChoreographEventHandler;
import ca.bc.gov.educ.api.course.constants.ActivityCode;
import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.messaging.MessagePublisher;
import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.nats.client.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EventHandlerDelegatorServiceTest {

    @Mock
    private ChoreographedEventPersistenceService choreographedEventPersistenceService;

    @Mock
    private ChoreographEventHandler choreographer;

    @Mock
    private EventHandlerService eventHandlerService;

    @Mock
    private MessagePublisher messagePublisher;
    
    @Mock
    private EducCourseApiConstants constants;
    
    @InjectMocks
    private EventHandlerDelegatorService delegatorService;

    @Mock
    private Message message;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleChoreographyEvent_NewEvent_AcknowledgedAndHandled() throws IOException {
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        choreographedEvent.setEventID(UUID.randomUUID());

        EventEntity eventEntity = EventEntity.builder().eventId(UUID.randomUUID()).build();

        when(choreographedEventPersistenceService.eventExistsInDB(any()))
                .thenReturn(Optional.empty());
        when(choreographedEventPersistenceService.persistEventToDB(any()))
                .thenReturn(eventEntity);
        when(message.getSubject()).thenReturn("COREG_EVENTS_TOPIC");

        delegatorService.handleChoreographyEvent(choreographedEvent, message);

        verify(message).ack();
    }

    @Test
    void testHandleChoreographyEvent_ExistingEvent_AcknowledgedOnly() throws IOException {
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        choreographedEvent.setEventID(UUID.randomUUID());

        when(choreographedEventPersistenceService.eventExistsInDB(any()))
                .thenReturn(Optional.of(EventEntity.builder().eventId(UUID.randomUUID()).build()));
        when(message.getSubject()).thenReturn("COREG_EVENTS_TOPIC");

        delegatorService.handleChoreographyEvent(choreographedEvent, message);

        verify(message).ack();
        verify(choreographer, never()).handleEvent(any());
    }

    @Test
    void testHandleEvent_SynchronousEvent() throws JsonProcessingException {
        var event = new ca.bc.gov.educ.api.course.model.dto.Event();
        event.setEventType(EventType.GET_STUDENT_COURSE.name());
        event.setReplyTo("replyChannel");

        byte[] fakeResponse = "response".getBytes();
        when(eventHandlerService.handleGetStudentCourseEvent(event)).thenReturn(fakeResponse);
        when(message.getReplyTo()).thenReturn("replyChannel");

        delegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage("replyChannel", fakeResponse);
    }

    @Test
    void testHandleEvent_AsynchronousEvent() throws JsonProcessingException {
        var event = new ca.bc.gov.educ.api.course.model.dto.Event();
        event.setEventType(EventType.GET_STUDENT_COURSE.name());
        event.setReplyTo("eventChannel");

        byte[] fakeResponse = "response".getBytes();
        when(eventHandlerService.handleGetStudentCourseEvent(event)).thenReturn(fakeResponse);
        when(message.getReplyTo()).thenReturn(null);

        delegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage("eventChannel", fakeResponse);
    }

    @Test
    void testSetActivityCode_ValidTopic() throws IOException {
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        when(message.getSubject()).thenReturn("COREG_EVENTS_TOPIC");

        EventEntity eventEntity = EventEntity.builder().eventId(UUID.randomUUID()).build();
        when(choreographedEventPersistenceService.eventExistsInDB(any())).thenReturn(Optional.empty());
        when(choreographedEventPersistenceService.persistEventToDB(any())).thenReturn(eventEntity);

        delegatorService.handleChoreographyEvent(choreographedEvent, message);

        assertEquals(ActivityCode.COREG_EVENT.name(), choreographedEvent.getActivityCode());
    }

    @Test
    void testSetActivityCode_InvalidTopic() throws IOException {
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        when(message.getSubject()).thenReturn("INVALID_TOPIC");

        EventEntity eventEntity = EventEntity.builder().eventId(UUID.randomUUID()).build();
        when(choreographedEventPersistenceService.eventExistsInDB(any())).thenReturn(Optional.empty());
        when(choreographedEventPersistenceService.persistEventToDB(any())).thenReturn(eventEntity);

        delegatorService.handleChoreographyEvent(choreographedEvent, message);

        assertNull(choreographedEvent.getActivityCode());
    }

    @Test
    void testHandleEvent_courseRequirementsSynchronousCase() throws Exception {
        String replyToChannel = "syncChannel";
        when(message.getReplyTo()).thenReturn(replyToChannel);

        Event event = new Event();
        event.setEventType("GET_COURSE_REQUIREMENTS");
        event.setEventId(UUID.randomUUID());
        event.setReplyTo(replyToChannel);
        event.setEventPayload("[\"CLE\",\"CLC\"]");

        byte[] dummyResponse = "requirements_response".getBytes(StandardCharsets.UTF_8);
        when(eventHandlerService.handleGetCourseRequirementsByCourseIDEvent(any())).thenReturn(dummyResponse);

        delegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage(replyToChannel, dummyResponse);
    }

    @Test
    void testHandleEvent_courseRequirementsAsynchronousCase() throws Exception {
        when(message.getReplyTo()).thenReturn(null);

        String asyncReplyChannel = "asyncChannel";
        Event event = new Event();
        event.setEventType("GET_COURSE_REQUIREMENTS");
        event.setEventId(UUID.randomUUID());
        event.setReplyTo(asyncReplyChannel);
        event.setEventPayload("[\"CLE\",\"CLC\"]");

        byte[] dummyResponse = "requirements_response".getBytes(StandardCharsets.UTF_8);
        when(eventHandlerService.handleGetCourseRequirementsByCourseIDEvent(event)).thenReturn(dummyResponse);

        delegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage(asyncReplyChannel, dummyResponse);
    }

    @Test
    void testHandleEvent_courseRestrictionsSynchronousCase() throws Exception {
        String replyToChannel = "syncChannel";
        when(message.getReplyTo()).thenReturn(replyToChannel);

        Event event = new Event();
        event.setEventType("GET_COURSE_RESTRICTIONS");
        event.setEventId(UUID.randomUUID());
        event.setReplyTo(replyToChannel);
        event.setEventPayload("[\"CLE\",\"CLC\"]");

        byte[] dummyResponse = "restrictions_response".getBytes(StandardCharsets.UTF_8);
        when(eventHandlerService.handleGetCourseRestrictionsByCourseIDEvent(event)).thenReturn(dummyResponse);

        delegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage(replyToChannel, dummyResponse);
    }

    @Test
    void testHandleEvent_courseRestrictionsAsynchronousCase() throws Exception {
        when(message.getReplyTo()).thenReturn(null);

        String asyncReplyChannel = "asyncChannel";
        Event event = new Event();
        event.setEventType("GET_COURSE_RESTRICTIONS");
        event.setEventId(UUID.randomUUID());
        event.setReplyTo(asyncReplyChannel);
        event.setEventPayload("[\"CLE\",\"CLC\"]");

        byte[] dummyResponse = "restrictions_response".getBytes(StandardCharsets.UTF_8);
        when(eventHandlerService.handleGetCourseRestrictionsByCourseIDEvent(event)).thenReturn(dummyResponse);

        delegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage(asyncReplyChannel, dummyResponse);
    }
}
