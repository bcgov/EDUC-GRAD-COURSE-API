package ca.bc.gov.educ.api.course.service;

import static lombok.AccessLevel.PRIVATE;

import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import ca.bc.gov.educ.api.course.struct.Event;
import ca.bc.gov.educ.api.course.util.JsonUtilWithJavaTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * The type Event handler service.
 */
@Service
@Slf4j
@SuppressWarnings("java:S3864")
public class EventHandlerService {

    /**
     * The constant NO_RECORD_SAGA_ID_EVENT_TYPE.
     */
    public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing.";
    /**
     * The constant RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE.
     */
    public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
            " just updating the db status so that it will be polled and sent back again.";
    /**
     * The constant PAYLOAD_LOG.
     */
    public static final String PAYLOAD_LOG = "payload is :: {}";
    /**
     * The constant EVENT_PAYLOAD.
     */
    public static final String EVENT_PAYLOAD = "event is :: {}";

    @Getter(PRIVATE)
    private final StudentCourseRepository studentCourseRepository;
    private final StudentCourseService studentCourseService;
    private final TraxStudentCourseService traxStudentCourseService;

    /**
     * Instantiates a new Event handler service.
     *
     */
    @Autowired
    public EventHandlerService(StudentCourseRepository studentCourseRepository, StudentCourseService studentCourseService, TraxStudentCourseService traxStudentCourseService) {
        this.studentCourseRepository = studentCourseRepository;
        this.studentCourseService = studentCourseService;
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
        // val trax = traxStudentCourseService.getStudentCourseList(event.getEventPayload(), false);
        // student id
        val studentCourseList = studentCourseService.getStudentCourses(UUID.fromString(event.getEventPayload()), false);
        log.debug("Optional course code list " + studentCourseList);
        log.debug("Returning " + studentCourseList);
        return JsonUtilWithJavaTime.getJsonBytesFromObject(studentCourseList);
    }
}
