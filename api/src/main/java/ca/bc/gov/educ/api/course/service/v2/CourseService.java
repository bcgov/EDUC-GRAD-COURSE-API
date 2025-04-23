package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.coreg.Courses;
import ca.bc.gov.educ.api.course.service.RESTService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utilize CoReg(Course Registration) API
 */
@Service("courseServiceV2")
@Slf4j
public class CourseService {

    private final RESTService restService;
    private final EducCourseApiConstants constants;
    private final WebClient gradCoregApiClient;

    @Autowired
    public CourseService(RESTService restService, EducCourseApiConstants constants, @Qualifier("gradCoregApiClient") WebClient gradCoregApiClient) {
        this.restService = restService;
        this.constants = constants;
        this.gradCoregApiClient = gradCoregApiClient;
    }

    public Course getCourseInfo(String courseID) {
        String url = String.format(constants.getCourseDetailByCourseIdUrl(), courseID);
        Courses course = restService.get(url, Courses.class, gradCoregApiClient);
        if (course != null) {
            return EducCourseApiUtils.convertCoregCourseIntoGradCourse(course);
        }
        return null;
    }
    public Course getCourseInfo(String courseCode, String courseLevel) {
        String externalCode = EducCourseApiUtils.getExternalCodeByCourseCodeAndLevel(courseCode, courseLevel);
        log.info("CoReg API lookup by external code: [{}]", externalCode);
        try {
            externalCode = URLEncoder.encode(externalCode, StandardCharsets.UTF_8).replace("+", "%20");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String url = String.format(constants.getCourseDetailByExternalCodeUrl(), externalCode);
        Courses course = restService.get(url, Courses.class, gradCoregApiClient);
        if (course != null) {
            return EducCourseApiUtils.convertCoregCourseIntoGradCourse(course);
        }
        return null;
    }

}
