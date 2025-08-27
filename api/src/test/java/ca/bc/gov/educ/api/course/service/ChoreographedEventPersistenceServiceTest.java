package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.constants.EventOutcome;
import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.model.StatusEvent;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.repository.EventRepository;
import ca.bc.gov.educ.api.course.repository.StatusEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static ca.bc.gov.educ.api.course.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.course.constants.EventStatus.MESSAGE_PUBLISHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChoreographedEventPersistenceServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private StatusEventRepository statusEventRepository;

    private ChoreographedEventPersistenceService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ChoreographedEventPersistenceService(eventRepository, statusEventRepository);
        eventRepository = mock(EventRepository.class);
        statusEventRepository = mock(StatusEventRepository.class);
        service = new ChoreographedEventPersistenceService(eventRepository, statusEventRepository);
    }

    @Test
    void testEventExistsInDB_found() {
        UUID eventId = UUID.randomUUID();
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        choreographedEvent.setEventID(eventId);
        EventEntity entity = new EventEntity();
        entity.setEventId(eventId);

        when(eventRepository.findByEventId(eventId)).thenReturn(Optional.of(entity));

        Optional<EventEntity> result = service.eventExistsInDB(choreographedEvent);

        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().getEventId());
    }

    @Test
    void testEventExistsInDB_notFound() {
        UUID eventId = UUID.randomUUID();
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        choreographedEvent.setEventID(eventId);

        when(eventRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        Optional<EventEntity> result = service.eventExistsInDB(choreographedEvent);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateEventStatus_eventPresent() {
        UUID eventId = UUID.randomUUID();
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        choreographedEvent.setEventID(eventId);

        EventEntity existingEntity = new EventEntity();
        existingEntity.setEventId(eventId);
        existingEntity.setEventStatus(DB_COMMITTED.toString());

        when(statusEventRepository.findByEventId(eventId)).thenReturn(Optional.ofNullable(StatusEvent.builder().eventId(eventId).build()));

        service.updateEventStatus(choreographedEvent);

        ArgumentCaptor<StatusEvent> captor = ArgumentCaptor.forClass(StatusEvent.class);
        verify(statusEventRepository).save(captor.capture());
        assertEquals(MESSAGE_PUBLISHED.toString(), captor.getValue().getEventStatus());
    }

    @Test
    void testUpdateEventStatus_eventNotPresent() {
        UUID eventId = UUID.randomUUID();
        ChoreographedEvent choreographedEvent = ChoreographedEvent.builder().eventID(eventId).build();

        when(statusEventRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        // Should not throw exception
        assertDoesNotThrow(() -> service.updateEventStatus(choreographedEvent));

        verify(statusEventRepository, never()).save(any());
    }

    @Test
    void testUpdateEventStatus_nullEvent() {
        assertDoesNotThrow(() -> service.updateEventStatus(null));
        verify(statusEventRepository, never()).save(any());
    }

    @Test
    void testPersistEventToDB_defaultsSet() {
        UUID eventId = UUID.randomUUID();
        // given
        ChoreographedEvent choreographedEvent = ChoreographedEvent.builder()
                .eventID(eventId)
                .eventType(EventType.COURSE_CREATED)
                .eventOutcome(EventOutcome.COURSE_CREATED)
                .eventPayload("{}") // non-null payload to prevent NPE
                .createUser(null)   // test default creation user
                .updateUser(null)   // test default update user
                .build();

        ArgumentCaptor<EventEntity> captor = ArgumentCaptor.forClass(EventEntity.class);
        when(eventRepository.save(any(EventEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        EventEntity saved = service.persistEventToDB(choreographedEvent);

        // then
        verify(eventRepository, times(1)).save(captor.capture());
        EventEntity capturedEntity = captor.getValue();

        assertNotNull(saved);
        assertEquals(eventId, capturedEntity.getEventId());
        assertEquals(EventType.COURSE_CREATED.name(), capturedEntity.getEventType());
        assertEquals(EventOutcome.COURSE_CREATED.name(), capturedEntity.getEventOutcome());
        assertEquals("{}", new String(capturedEntity.getEventPayload())); // bytes converted back
        assertEquals("API_COURSE", capturedEntity.getCreateUser());  // default
        assertEquals("API_COURSE", capturedEntity.getUpdateUser());  // default
        assertNotNull(capturedEntity.getCreateDate());
        assertNotNull(capturedEntity.getUpdateDate());
    }

    @Test
    void testPersistEventToDB_withProvidedUsers() {
        UUID eventId = UUID.randomUUID();
        // given
        ChoreographedEvent choreographedEvent = ChoreographedEvent.builder()
                .eventID(eventId)
                .eventType(EventType.COURSE_CREATED)
                .eventOutcome(EventOutcome.COURSE_CREATED)
                .eventPayload("{\"key\":\"value\"}")
                .createUser("customCreator")
                .updateUser("customUpdater")
                .build();

        when(eventRepository.save(any(EventEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        EventEntity saved = service.persistEventToDB(choreographedEvent);

        // then
        assertEquals("customCreator", saved.getCreateUser());
        assertEquals("customUpdater", saved.getUpdateUser());
        assertEquals("{\"key\":\"value\"}", new String(saved.getEventPayload()));
    }
}
