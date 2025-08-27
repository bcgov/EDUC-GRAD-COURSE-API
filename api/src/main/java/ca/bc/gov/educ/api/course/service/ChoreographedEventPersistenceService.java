package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.repository.EventRepository;
import ca.bc.gov.educ.api.course.repository.StatusEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static ca.bc.gov.educ.api.course.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.course.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.course.util.EducCourseApiConstants.DEFAULT_CREATED_BY;
import static ca.bc.gov.educ.api.course.util.EducCourseApiConstants.DEFAULT_UPDATED_BY;

@Service
@Slf4j
public class ChoreographedEventPersistenceService {
  private final EventRepository eventRepository;
  private final StatusEventRepository statusEventRepository;

  @Autowired
  public ChoreographedEventPersistenceService(
          final EventRepository eventRepository,
          final StatusEventRepository statusEventRepository) {
    this.eventRepository = eventRepository;
    this.statusEventRepository = statusEventRepository;
  }

  public Optional<EventEntity> eventExistsInDB(final ChoreographedEvent choreographedEvent) {
    return eventRepository.findByEventId(choreographedEvent.getEventID());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public EventEntity persistEventToDB(final ChoreographedEvent choreographedEvent) {
    final EventEntity eventEntity = EventEntity.builder()
              .eventType(choreographedEvent.getEventType().toString())
              .eventId(choreographedEvent.getEventID())
              .eventOutcome(choreographedEvent.getEventOutcome().toString())
              .eventPayload(choreographedEvent.getEventPayload())
              .eventStatus(DB_COMMITTED.toString())
              .createUser(StringUtils.isBlank(choreographedEvent.getCreateUser()) ? DEFAULT_CREATED_BY : choreographedEvent.getCreateUser())
              .updateUser(StringUtils.isBlank(choreographedEvent.getUpdateUser()) ? DEFAULT_UPDATED_BY : choreographedEvent.getUpdateUser())
              .createDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .build();
      return this.eventRepository.save(eventEntity);
  }

  /**
   * Update event status.
   *
   * @param choreographedEvent the choreographed eventw
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateEventStatus(ChoreographedEvent choreographedEvent) {
    if (choreographedEvent != null && choreographedEvent.getEventID() != null) {
      var eventOptional = statusEventRepository.findByEventId(choreographedEvent.getEventID());
      if (eventOptional.isPresent()) {
        var courseUpdatedPubEvent = eventOptional.get();
        courseUpdatedPubEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
        statusEventRepository.save(courseUpdatedPubEvent);
      }
    }
  }
}
