package ca.bc.gov.educ.api.course.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.messaging.MessagePublisher;
import ca.bc.gov.educ.api.course.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.course.struct.Event;
import io.nats.client.Message;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Slf4j
public class EventHandlerDelegatorServiceTest {

    private EventHandlerDelegatorService eventHandlerDelegatorService;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private EventHandlerService eventHandlerService;

    @Mock
    private Publisher publisher;

    @Mock
    private Message message;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventHandlerDelegatorService = new EventHandlerDelegatorService(messagePublisher, eventHandlerService, publisher);
    }

    static final String pen = "123456789";

    @Test
    public void testHandleEvent_synchronousCase() throws Exception {
        String replyToChannel = "syncChannel";
        when(message.getReplyTo()).thenReturn(replyToChannel);

        Event event = Event.builder()
                .eventType(EventType.GET_STUDENT_COURSE)
                .sagaId(UUID.randomUUID())
                .replyTo(replyToChannel)
                .eventPayload(pen)
                .build();

        byte[] dummyResponse = "response".getBytes(StandardCharsets.UTF_8);
        when(eventHandlerService.handleGetStudentCourseEvent(event)).thenReturn(dummyResponse);

        eventHandlerDelegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage(replyToChannel, dummyResponse);
    }

    @Test
    public void testHandleEvent_asynchronousCase() throws Exception {
        when(message.getReplyTo()).thenReturn(null);

        String asyncReplyChannel = "asyncChannel";
        Event event = Event.builder()
                .eventType(EventType.GET_STUDENT_COURSE)
                .sagaId(UUID.randomUUID())
                .replyTo(asyncReplyChannel)
                .eventPayload(pen)
                .build();

        byte[] dummyResponse = "response".getBytes(StandardCharsets.UTF_8);
        when(eventHandlerService.handleGetStudentCourseEvent(event)).thenReturn(dummyResponse);

        eventHandlerDelegatorService.handleEvent(event, message);

        verify(messagePublisher).dispatchMessage(asyncReplyChannel, dummyResponse);
    }

    @Test
    public void testHandleEvent_exceptionHandling() throws Exception {
        Event event = Event.builder()
                .eventType(EventType.GET_STUDENT_COURSE)
                .sagaId(UUID.randomUUID())
                .replyTo("channel")
                .eventPayload(pen)
                .build();

        when(eventHandlerService.handleGetStudentCourseEvent(event))
                .thenThrow(new RuntimeException("Test Exception"));

        eventHandlerDelegatorService.handleEvent(event, message);

        verify(messagePublisher, never()).dispatchMessage(anyString(), any(byte[].class));
    }
}
