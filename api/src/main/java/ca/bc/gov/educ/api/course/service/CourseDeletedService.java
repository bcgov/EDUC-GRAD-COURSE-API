package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.constants.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CourseDeletedService extends CourseEventsService {

    @Override
    public String getEventType() {
        return EventType.COURSE_DELETED.name();
    }
}
