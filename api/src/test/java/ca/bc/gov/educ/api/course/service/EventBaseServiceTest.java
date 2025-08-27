package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.constants.EventStatus;
import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.repository.EventHistoryRepository;
import ca.bc.gov.educ.api.course.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventBaseServiceTest {

    private EventRepository eventRepository;
    private EventHistoryRepository eventHistoryRepository;
    private EventBaseService<EventEntity> service;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventHistoryRepository = mock(EventHistoryRepository.class);

        // Create an anonymous subclass implementing abstract methods
        service = new EventBaseService<>() {
            @Override
            public void processEvent(EventEntity event) {
                // Not needed for this test
            }

            @Override
            public String getEventType() {
                return EventType.COURSE_UPDATED.name();
            }
        };

        service.eventRepository = eventRepository;
        service.eventHistoryRepository = eventHistoryRepository;
    }

    @Test
    void testUpdateEvent_eventExists_includesHistory() {
        UUID eventId = UUID.randomUUID();
        EventEntity existingEvent = new EventEntity();
        existingEvent.setEventId(eventId);
        existingEvent.setEventStatus("NEW");

        when(eventRepository.findByEventId(eventId)).thenReturn(Optional.of(existingEvent));

        EventEntity update = new EventEntity();
        update.setEventId(eventId);

        service.updateEvent(update, true);

        // Verify event status updated and saved
        ArgumentCaptor<EventEntity> eventCaptor = ArgumentCaptor.forClass(EventEntity.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertEquals(EventStatus.PROCESSED.toString(), eventCaptor.getValue().getEventStatus());
        assertNotNull(eventCaptor.getValue().getUpdateDate());

        // Verify history saved
        ArgumentCaptor<EventHistoryEntity> historyCaptor = ArgumentCaptor.forClass(EventHistoryEntity.class);
        verify(eventHistoryRepository).save(historyCaptor.capture());
        assertEquals(existingEvent, historyCaptor.getValue().getEvent());
    }

    @Test
    void testUpdateEvent_eventExists_withoutHistory() {
        UUID eventId = UUID.randomUUID();
        EventEntity existingEvent = new EventEntity();
        existingEvent.setEventId(eventId);
        existingEvent.setEventStatus("NEW");

        when(eventRepository.findByEventId(eventId)).thenReturn(Optional.of(existingEvent));

        EventEntity update = new EventEntity();
        update.setEventId(eventId);

        service.updateEvent(update, false);

        verify(eventRepository).save(any(EventEntity.class));
        verify(eventHistoryRepository, never()).save(any());
    }

    @Test
    void testUpdateEvent_eventNotFound() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        EventEntity update = new EventEntity();
        update.setEventId(eventId);

        // Should not throw, just do nothing
        service.updateEvent(update, true);

        verify(eventRepository, never()).save(any());
        verify(eventHistoryRepository, never()).save(any());
    }
}
