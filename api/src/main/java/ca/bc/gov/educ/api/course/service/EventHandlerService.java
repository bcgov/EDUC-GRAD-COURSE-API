package ca.bc.gov.educ.api.course.service;

import static lombok.AccessLevel.PRIVATE;

import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import ca.bc.gov.educ.api.course.util.JsonUtilWithJavaTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * The type Event handler service.
 */
@Service
@Slf4j
public class EventHandlerService {

    /**
     * The constant PAYLOAD_LOG.
     */
    public static final String PAYLOAD_LOG = "payload is :: {}";

    @Getter(PRIVATE)
    private final StudentCourseRepository studentCourseRepository;
    private final TraxStudentCourseService traxStudentCourseService;

    /**
     * Instantiates a new Event handler service.
     *
     */
    @Autowired
    public EventHandlerService(StudentCourseRepository studentCourseRepository, TraxStudentCourseService traxStudentCourseService) {
        this.studentCourseRepository = studentCourseRepository;
        this.traxStudentCourseService = traxStudentCourseService;
    }

    /**
     * Saga should never be null for this type of event.
     * this method expects that the event payload contains a pen number.
     *
     * @param event         containing the Course External ID.
     * @return the byte [ ]
     * @throws JsonProcessingException the json processing exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public byte[] handleGetStudentCourseEvent(Event event) throws JsonProcessingException {
        // always synchronous
        // pen
         val studentCourseList = traxStudentCourseService.getStudentCourseList(event.getEventPayload(), false);
         // TODO - please notify edx team before changing
        // student id - for when v2 is complete
        //val studentCourseList = studentCourseService.getStudentCourses(UUID.fromString(event.getEventPayload()), false);
        log.debug("Returning " + studentCourseList);
        return JsonUtilWithJavaTime.getJsonBytesFromObject(studentCourseList);
    }
}
