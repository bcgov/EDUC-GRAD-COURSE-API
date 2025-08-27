package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.repository.StatusEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import static ca.bc.gov.educ.api.course.constants.EventStatus.MESSAGE_PUBLISHED;

/**
 * This class will process events from Jet Stream, which is used in choreography pattern, where messages are published if a grad course status is created or updated.
 */
@Service
@Slf4j
public class JetStreamEventHandlerService {

    private final StatusEventRepository StatusEventRepository;


    /**
     * Instantiates a new Stan event handler service.
     *
     * @param StatusEventRepository the coreg status event repository
     */
    @Autowired
    public JetStreamEventHandlerService(StatusEventRepository StatusEventRepository) {
        this.StatusEventRepository = StatusEventRepository;
    }

    /**
     * Update event status.
     *
     * @param choreographedEvent the choreographed event
     */
    @Transactional
    public void updateEventStatus(ChoreographedEvent choreographedEvent) {
        if (choreographedEvent != null && choreographedEvent.getEventID() != null) {
            var eventID = choreographedEvent.getEventID();
            var eventOptional = StatusEventRepository.findById(eventID);
            if (eventOptional.isPresent()) {
                var gradCourseEvent = eventOptional.get();
                gradCourseEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
                StatusEventRepository.save(gradCourseEvent);
            }
        }
    }
}
