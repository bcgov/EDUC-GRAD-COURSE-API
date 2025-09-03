package ca.bc.gov.educ.api.course.service;

import static lombok.AccessLevel.PRIVATE;

import ca.bc.gov.educ.api.course.model.transformer.CourseRequirementTransformer;
import ca.bc.gov.educ.api.course.model.transformer.CourseRestrictionsTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRequirementRepository;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import ca.bc.gov.educ.api.course.util.JsonUtilWithJavaTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Event handler service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventHandlerService {

    /**
     * The constant PAYLOAD_LOG.
     */
    public static final String PAYLOAD_LOG = "payload is :: {}";

    @Getter(PRIVATE)
    private final StudentCourseRepository studentCourseRepository;
    private final TraxStudentCourseService traxStudentCourseService;
    private final CourseRequirementTransformer courseRequirementTransformer;
    private final CourseRequirementRepository courseRequirementRepository;
    private final CourseRestrictionsTransformer courseRestrictionTransformer;
    private final CourseRestrictionRepository courseRestrictionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public byte[] handleGetCourseRequirementsByCourseIDEvent(Event event) throws JsonProcessingException {
        List<String> courseCodes = objectMapper.readValue(
            event.getEventPayload(),
            new TypeReference<>() {
            }
        );
        val courses =  courseRequirementTransformer.transformToDTO(courseRequirementRepository.findByCourseCodeIn(courseCodes));
        return JsonUtilWithJavaTime.getJsonBytesFromObject(courses);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public byte[] handleGetCourseRestrictionsByCourseIDEvent(Event event) throws JsonProcessingException {
        List<String> courseCodes = objectMapper.readValue(
            event.getEventPayload(),
            new TypeReference<>() {
            }
        );
        val courses =  courseRestrictionTransformer.transformToDTO(
            courseRestrictionRepository.findByMainCourseIn(courseCodes));
        return JsonUtilWithJavaTime.getJsonBytesFromObject(courses);
    }
}
