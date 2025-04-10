package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseAllowableCredits;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseCharacteristics;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseCode;
import ca.bc.gov.educ.api.course.model.dto.coreg.Courses;
import ca.bc.gov.educ.api.course.service.RESTService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CourseServiceTest {

    @Autowired
    CourseService courseServiceV2;

    @MockBean
    RESTService restService;

    @Autowired
    EducCourseApiConstants constants;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    @Qualifier("default")
    public WebClient webClient;

    @MockBean
    @Qualifier("gradCoregClient")
    public WebClient coregWebClient;

    @Test
    public void testGetCourseInfoByCourseID_when_itDoesNot_Exist() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");
        course.setNumCredits(4);

        String url = String.format(constants.getCourseDetailByCourseIdUrl(), course.getCourseID());
        when(restService.get(url, Courses.class,webClient)).thenReturn(null);

        var result = courseServiceV2.getCourseInfo(course.getCourseID());
        assertThat(result).isNull();
    }

    @Test
    public void testGetCourseInfoByCourseID() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");
        course.setNumCredits(4);

        Courses coregCourse = new Courses();
        coregCourse.setCourseID(course.getCourseID());
        // CourseCharacteristics
        CourseCharacteristics courseCharacteristics = new CourseCharacteristics();
        courseCharacteristics.setCode("ENG");
        courseCharacteristics.setDescription("English");
        coregCourse.setCourseCharacteristics(courseCharacteristics);
        // CourseCode
        CourseCode courseCode = new CourseCode();
        courseCode.setOriginatingSystem("39");
        courseCode.setCourseID(course.getCourseID());
        courseCode.setExternalCode(EducCourseApiUtils.getExternalCodeByCourseCodeAndLevel(course.getCourseCode(), course.getCourseLevel()));
        coregCourse.setCourseCode(Arrays.asList(courseCode));
        // AllowableCredits
        CourseAllowableCredits credit1 = new CourseAllowableCredits();
        credit1.setCourseID(course.getCourseID());
        credit1.setCreditValue("3");
        CourseAllowableCredits credit2 = new CourseAllowableCredits();
        credit2.setCourseID(course.getCourseID());
        credit2.setCreditValue("4");
        coregCourse.setCourseAllowableCredit(Arrays.asList(credit1, credit2));

        String url = String.format(constants.getCourseDetailByCourseIdUrl(), course.getCourseID());
        when(restService.get(url, Courses.class, coregWebClient)).thenReturn(coregCourse);

        var result = courseServiceV2.getCourseInfo(course.getCourseID());
        assertThat(result).isNotNull();
        assertThat(result.getCourseID()).isEqualTo(course.getCourseID());
        assertThat(result.getCourseCode()).isEqualTo(course.getCourseCode());
        assertThat(result.getCourseLevel()).isEqualTo(course.getCourseLevel());
        assertThat(result.getNumCredits()).isEqualTo(course.getNumCredits());
    }

    @Test
    public void testGetCourseInfoByCourseCodeAndCourseLevel() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");
        course.setNumCredits(4);

        Courses coregCourse = new Courses();
        coregCourse.setCourseID(course.getCourseID());
        // CourseCharacteristics
        CourseCharacteristics courseCharacteristics = new CourseCharacteristics();
        courseCharacteristics.setCode("ENG");
        courseCharacteristics.setDescription("English");
        coregCourse.setCourseCharacteristics(courseCharacteristics);
        // CourseCode
        CourseCode courseCode = new CourseCode();
        courseCode.setOriginatingSystem("39");
        courseCode.setCourseID(course.getCourseID());
        courseCode.setExternalCode(EducCourseApiUtils.getExternalCodeByCourseCodeAndLevel(course.getCourseCode(), course.getCourseLevel()));
        coregCourse.setCourseCode(Arrays.asList(courseCode));
        // AllowableCredits
        CourseAllowableCredits credit1 = new CourseAllowableCredits();
        credit1.setCourseID(course.getCourseID());
        credit1.setCreditValue("3");
        CourseAllowableCredits credit2 = new CourseAllowableCredits();
        credit2.setCourseID(course.getCourseID());
        credit2.setCreditValue("4");
        coregCourse.setCourseAllowableCredit(Arrays.asList(credit1, credit2));

        when(restService.get(anyString(), eq(Courses.class), eq(coregWebClient))).thenReturn(coregCourse);

        var result = courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel());
        assertThat(result).isNotNull();
        assertThat(result.getCourseID()).isEqualTo(course.getCourseID());
        assertThat(result.getCourseCode()).isEqualTo(course.getCourseCode());
        assertThat(result.getCourseLevel()).isEqualTo(course.getCourseLevel());
        assertThat(result.getNumCredits()).isEqualTo(course.getNumCredits());
    }
}
