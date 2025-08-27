package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public abstract class CourseEventsService extends EventBaseService<EventEntity> {

    public void processEvent(EventEntity eventEntity) {
        log.debug("Processing event {}", eventEntity.getEventType());
        try{
            this.updateEvent(eventEntity, true);
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
    }
}
