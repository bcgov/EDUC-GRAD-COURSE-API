package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentCourseEntity;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentCourseId;
import ca.bc.gov.educ.api.course.repository.TraxStudentCourseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TraxTraxStudentCourseServiceTest {

    @Autowired
    TraxStudentCourseService traxStudentCourseService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private TraxStudentCourseRepository studentCourseRepo;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    @Qualifier("courseApiClient")
    public WebClient courseApiWebClient;

    @MockBean
    @Qualifier("gradCoregApiClient")
    public WebClient coregApiWebClient;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.RequestBodySpec requestBodyMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;

    @Test
    public void testGetStudentCourseList_whenGivenCourseCodeAndLevelWithRelatedCourse_thenReturnSuccess() {
        // ID
        TraxStudentCourseId traxStudentCourseId = new TraxStudentCourseId();
        traxStudentCourseId.setPen("123456789");
        traxStudentCourseId.setCourseCode("main");
        traxStudentCourseId.setCourseLevel("12");
        traxStudentCourseId.setSessionDate("2020-05");

        TraxStudentCourseEntity traxStudentCourseEntity = new TraxStudentCourseEntity();
        traxStudentCourseEntity.setCourseKey(traxStudentCourseId);
        traxStudentCourseEntity.setRelatedCourse("rest");
        traxStudentCourseEntity.setRelatedLevel("12");

        Course course = new Course();
        course.setCourseCode("main");
        course.setCourseLevel("12");
        course.setCourseName("test main course");
        course.setGenericCourseType("gct");
        course.setLanguage("en");

        when(studentCourseRepo.findByPen(traxStudentCourseId.getPen())).thenReturn(Arrays.asList(traxStudentCourseEntity));
        when(courseService.getCourseDetails(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);
        when(courseService.getCourseDetails(traxStudentCourseEntity.getRelatedCourse(), traxStudentCourseEntity.getRelatedLevel())).thenReturn(course);

        var result = traxStudentCourseService.getStudentCourseList(traxStudentCourseId.getPen(), true);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        TraxStudentCourse responseTraxStudentCourse = result.get(0);
        assertThat(responseTraxStudentCourse.getCourseCode()).isEqualTo(course.getCourseCode());
        assertThat(responseTraxStudentCourse.getGenericCourseType()).isEqualTo(course.getGenericCourseType());
    }

    @Test
    public void testGetStudentCourseList_whenGivenCourseCodeWithoutRelatedCourse_thenReturnSuccess() {
        // ID
        TraxStudentCourseId traxStudentCourseId = new TraxStudentCourseId();
        traxStudentCourseId.setPen("123456789");
        traxStudentCourseId.setCourseCode("main");
        traxStudentCourseId.setCourseLevel("");
        traxStudentCourseId.setSessionDate("2020-05");

        TraxStudentCourseEntity traxStudentCourseEntity = new TraxStudentCourseEntity();
        traxStudentCourseEntity.setCourseKey(traxStudentCourseId);

        Course course = new Course();
        course.setCourseCode("main");
        course.setCourseLevel("12");
        course.setCourseName("test main course");
        course.setGenericCourseType("gct");
        course.setLanguage("en");

        when(studentCourseRepo.findByPen(traxStudentCourseId.getPen())).thenReturn(Arrays.asList(traxStudentCourseEntity));
        when(courseService.getCourseDetails(course.getCourseCode(), " ")).thenReturn(course);

        var result = traxStudentCourseService.getStudentCourseList(traxStudentCourseId.getPen(), true);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        TraxStudentCourse responseTraxStudentCourse = result.get(0);
        assertThat(responseTraxStudentCourse.getCourseCode()).isEqualTo(course.getCourseCode());
        assertThat(responseTraxStudentCourse.getGenericCourseType()).isEqualTo(course.getGenericCourseType());
    }

    @Test
    public void checkFrenchImmersionCourse() {
        String pen = "123456789";
        when(studentCourseRepo.countFrenchImmersionCourses(pen)).thenReturn(1L);

        var result = traxStudentCourseService.checkFrenchImmersionCourse(pen);
        assertThat(result).isTrue();
    }

    @Test
    public void checkFrenchImmersionCourseByPenAndCourseLevel() {
        String pen = "123456789";
        String courseLevel = "11";
        when(studentCourseRepo.countFrenchImmersionCourses(pen, courseLevel)).thenReturn(1L);

        var result = traxStudentCourseService.checkFrenchImmersionCourse(pen, courseLevel);
        assertThat(result).isTrue();
    }

    @Test
    public void checkFrenchImmersionCourseFor1986ENByPenAndCourseLevel() {
        String pen = "123456789";
        String courseLevel = "11";
        when(studentCourseRepo.countFrenchImmersionCourse(pen, courseLevel)).thenReturn(1L);

        var result = traxStudentCourseService.checkFrenchImmersionCourseForEN(pen, courseLevel);
        assertThat(result).isTrue();
    }

}

