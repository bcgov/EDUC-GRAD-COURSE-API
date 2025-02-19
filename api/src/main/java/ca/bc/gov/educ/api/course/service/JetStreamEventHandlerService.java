package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.repository.GradCourseStatusEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.UUID;

import static ca.bc.gov.educ.api.course.constants.EventStatus.MESSAGE_PUBLISHED;

/**
 * This class will process events from Jet Stream, which is used in choreography pattern, where messages are published if a grad course status is created or updated.
 */
@Service
@Slf4j
public class JetStreamEventHandlerService {

    private final GradCourseStatusEventRepository gradCourseStatusEventRepository;


    /**
     * Instantiates a new Stan event handler service.
     *
     * @param gradCourseStatusEventRepository the coreg status event repository
     */
    @Autowired
    public JetStreamEventHandlerService(GradCourseStatusEventRepository gradCourseStatusEventRepository) {
        this.gradCourseStatusEventRepository = gradCourseStatusEventRepository;
    }

    /**
     * Update event status.
     *
     * @param choreographedEvent the choreographed event
     */
    @Transactional
    public void updateEventStatus(ChoreographedEvent choreographedEvent) {
        if (choreographedEvent != null && choreographedEvent.getEventID() != null) {
            var eventID = UUID.fromString(choreographedEvent.getEventID());
            var eventOptional = gradCourseStatusEventRepository.findById(eventID);
            if (eventOptional.isPresent()) {
                var gradCourseEvent = eventOptional.get();
                gradCourseEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
                gradCourseStatusEventRepository.save(gradCourseEvent);
            }
        }
    }
}
