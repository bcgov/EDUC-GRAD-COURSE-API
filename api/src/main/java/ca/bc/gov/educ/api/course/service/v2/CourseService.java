package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseDetail;
import ca.bc.gov.educ.api.course.model.dto.RestResponsePage;
import ca.bc.gov.educ.api.course.model.dto.search.*;
import ca.bc.gov.educ.api.course.model.dto.coreg.Courses;
import ca.bc.gov.educ.api.course.service.RESTService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utilize CoReg(Course Registration) API
 */
@Service("courseServiceV2")
@Slf4j
public class CourseService {

    private static final String PAGE_NUMBER="pageNumber";
    private static final String PAGE_SIZE="pageSize";
    private static final String SEARCH_CRITERIA_LIST = "searchCriteriaList";

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
        log.debug("CoReg API lookup by external code: [{}]", externalCode);
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

    public List<CourseDetail> getCourseDetails(List<String> courseIDs) {
        List<CourseDetail> courses = new ArrayList<>();
        int pageNumber = 0;
        int pageSize = 1000;
        if(!CollectionUtils.isEmpty(courseIDs)) {
            List<Search> searches = new LinkedList<>();
            List<SearchCriteria> criteriaList = new ArrayList<>();
            criteriaList.add(SearchCriteria.builder().condition(Condition.AND).key("courseID").operation(FilterOperation.IN).value(String.join(",",courseIDs)).valueType(ValueType.STRING).build());
            searches.add(Search.builder().condition(Condition.AND).searchCriteriaList(criteriaList).build());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String criteriaJSON = objectMapper.writeValueAsString(searches);
                String encodedURL = URLEncoder.encode(criteriaJSON, StandardCharsets.UTF_8.toString());
                RestResponsePage<Courses> response = gradCoregApiClient.get().uri(constants.getCourseDetailSearchUrl(),
                                uri -> uri
                                        .queryParam(PAGE_NUMBER, pageNumber)
                                        .queryParam(PAGE_SIZE, pageSize)
                                        .queryParam(SEARCH_CRITERIA_LIST, encodedURL)
                                        .build())
                        .retrieve().bodyToMono(new ParameterizedTypeReference<RestResponsePage<Courses>>() {}).block();
                for(Courses course: response.getContent()) {

                    courses.add(EducCourseApiUtils.convertCoregCourseIntoGradCourseDetail(course));
                }
                return courses;
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
        return Collections.emptyList();
    }

}
