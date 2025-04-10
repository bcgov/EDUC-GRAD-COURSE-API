package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.entity.CourseEntity;
import ca.bc.gov.educ.api.course.model.entity.CourseId;
import ca.bc.gov.educ.api.course.repository.CourseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    @Qualifier("default")
    public WebClient webClient;

    @Test
    public void testGetCourseList() {
        // Course
        CourseEntity course1 = new CourseEntity();
        course1.setCourseKey(new CourseId("Test1", "12"));
        course1.setCourseName("Test1 Name");

        CourseEntity course2 = new CourseEntity();
        course2.setCourseKey(new CourseId("Test2", "12"));
        course2.setCourseName("Test2 Name");

        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));
        var result = courseService.getCourseList();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        Course responseCourse1 = result.get(0);
        assertThat(responseCourse1.getCourseCode()).isEqualTo(course1.getCourseKey().getCourseCode());
        assertThat(responseCourse1.getCourseLevel()).isEqualTo(course1.getCourseKey().getCourseLevel());

        Course responseCourse2 = result.get(1);
        assertThat(responseCourse2.getCourseCode()).isEqualTo(course2.getCourseKey().getCourseCode());
        assertThat(responseCourse2.getCourseLevel()).isEqualTo(course2.getCourseKey().getCourseLevel());
    }

    @Test
    public void testGetCourseDetails() {
        // Course
        CourseEntity course = new CourseEntity();
        CourseId courseKey = new CourseId("Test", "12");
        course.setCourseKey(courseKey);
        course.setCourseName("Test Name");

        when(courseRepository.findByCourseKey(courseKey)).thenReturn(course);
        var result = courseService.getCourseDetails(courseKey.getCourseCode(), courseKey.getCourseLevel());
        assertThat(result).isNotNull();
        assertThat(result.getCourseCode()).isEqualTo(courseKey.getCourseCode());
        assertThat(result.getCourseLevel()).isEqualTo(courseKey.getCourseLevel());
    }

    @Test
    public void testGetCourseSearchList() {
        // Course
        CourseEntity course = new CourseEntity();
        CourseId courseKey = new CourseId("Test", "12");
        course.setCourseKey(courseKey);
        course.setCourseName("Test Name");
        course.setStartDate(new Date(System.currentTimeMillis() - 10000L));
        course.setEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(Arrays.asList(course));
        var result = courseService.getCourseSearchList(courseKey.getCourseCode(), courseKey.getCourseLevel(), null, null,
                course.getStartDate(), course.getEndDate());
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        Course responseCourse = result.get(0);
        assertThat(responseCourse.getCourseCode()).isEqualTo(courseKey.getCourseCode());
        assertThat(responseCourse.getCourseLevel()).isEqualTo(courseKey.getCourseLevel());
    }

    @Test
    public void checkBlankLanguageCourseByCourseCodeAndCourseLevel() {
        String courseCode = "SMA";
        String courseLevel = "12";
        when(courseRepository.countTabCourses(courseCode, courseLevel, " ")).thenReturn(1L);

        var result = courseService.hasBlankLanguageCourse(courseCode, courseLevel);
        assertThat(result).isTrue();
    }

    @Test
    public void checkFrenchLanguageCourseByCourseCodeAndCourseLevel() {
        String courseCode = "FRALP";
        String courseLevel = "12";
        when(courseRepository.countTabCourses(courseCode, courseLevel, "F")).thenReturn(1L);

        var result = courseService.hasFrenchLanguageCourse(courseCode, courseLevel);
        assertThat(result).isTrue();
    }
}
