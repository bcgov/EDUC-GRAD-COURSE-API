package ca.bc.gov.educ.api.course.choreographer;

import ca.bc.gov.educ.api.course.constants.EventStatus;
import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.repository.EventRepository;
import ca.bc.gov.educ.api.course.service.EventService;
import ca.bc.gov.educ.api.course.util.JsonUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static ca.bc.gov.educ.api.course.constants.EventType.*;

/**
 * This class is responsible to handle different choreographed events related student by calling different services.
 */

@Component
@Slf4j
public class ChoreographEventHandler {
  private final Executor eventExecutor;
  private final Map<String, EventService> eventServiceMap;

  private final EventRepository eventRepository;

  public ChoreographEventHandler(final List<EventService> eventServices, final EventRepository eventRepository) {
    this.eventServiceMap = new HashMap<>();
    this.eventRepository = eventRepository;
    this.eventExecutor = new EnhancedQueueExecutor.Builder()
            .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("event-executor-%d").build())
            .setCorePoolSize(10).setMaximumPoolSize(20).setKeepAliveTime(Duration.ofSeconds(60)).build();
    eventServices.forEach(eventService -> this.eventServiceMap.put(eventService.getEventType(), eventService));
  }

  public void handleEvent(@NonNull final EventEntity eventEntity) {
    //only one thread will process all the request. since RDB won't handle concurrent requests.
    this.eventExecutor.execute(() -> {
      try {
        switch (EventType.valueOf(eventEntity.getEventType())) {
          case COURSE_CREATED -> {
            log.debug("Processing COURSE_CREATED eventEntity record :: {} ", eventEntity);
            this.eventServiceMap.get(COURSE_CREATED.toString()).processEvent(eventEntity);
          }
          case COURSE_UPDATED -> {
            log.debug("Processing COURSE_UPDATED eventEntity record :: {} ", eventEntity);
            this.eventServiceMap.get(COURSE_UPDATED.toString()).processEvent(eventEntity);
          }
          case COURSE_DELETED -> {
            log.debug("Processing COURSE_DELETED eventEntity record :: {} ", eventEntity);
            this.eventServiceMap.get(COURSE_DELETED.toString()).processEvent(eventEntity);
          }
          default -> {
            log.warn("Silently ignoring eventEntity: {}", eventEntity);
            this.eventRepository.findByEventId(eventEntity.getEventId()).ifPresent(existingEvent -> {
              existingEvent.setEventStatus(EventStatus.PROCESSED.toString());
              existingEvent.setUpdateDate(LocalDateTime.now());
              this.eventRepository.save(existingEvent);
            });
            break;
          }
        }
      } catch (final Exception exception) {
        log.error("Exception while processing eventEntity :: {}", eventEntity, exception);
      }
    });


  }
}
