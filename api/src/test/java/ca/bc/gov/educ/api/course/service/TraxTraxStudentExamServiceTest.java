package ca.bc.gov.educ.api.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.TraxStudentExam;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentExamEntity;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentExamId;
import ca.bc.gov.educ.api.course.repository.TraxStudentExamRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TraxTraxStudentExamServiceTest {

    @Autowired
    TraxStudentExamService traxStudentExamService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private TraxStudentExamRepository studentExamRepo;

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
    public void testGetStudentExamList_whenGivenCourseCodeAndLevelWithRelatedCourse_thenReturnSuccess() {
        // ID
        TraxStudentExamId traxStudentExamId = new TraxStudentExamId();
        traxStudentExamId.setPen("123456789");
        traxStudentExamId.setCourseCode("main");
        traxStudentExamId.setCourseLevel("12");
        traxStudentExamId.setSessionDate("2020-05");

        TraxStudentExamEntity traxStudentExamEntity = new TraxStudentExamEntity();
        traxStudentExamEntity.setCourseKey(traxStudentExamId);

        Course course = new Course();
        course.setCourseCode("main");
        course.setCourseLevel("12");
        course.setCourseName("test main course");
        course.setGenericCourseType("gct");
        course.setLanguage("en");

        when(studentExamRepo.findByPen(traxStudentExamId.getPen())).thenReturn(Arrays.asList(traxStudentExamEntity));
        when(courseService.getCourseDetails(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);

        var result = traxStudentExamService.getStudentExamList(traxStudentExamId.getPen(), true);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        TraxStudentExam responseTraxStudentExam = result.get(0);
        assertThat(responseTraxStudentExam.getCourseCode()).isEqualTo(course.getCourseCode());
    }

    @Test
    public void testGetStudentExamList_whenGivenCourseCodeWithoutRelatedCourse_thenReturnSuccess() {
        // ID
    	TraxStudentExamId traxStudentExamId = new TraxStudentExamId();
        traxStudentExamId.setPen("123456789");
        traxStudentExamId.setCourseCode("main");
        traxStudentExamId.setCourseLevel("");
        traxStudentExamId.setSessionDate("2020-05");

        TraxStudentExamEntity traxStudentExamEntity = new TraxStudentExamEntity();
        traxStudentExamEntity.setCourseKey(traxStudentExamId);

        Course course = new Course();
        course.setCourseCode("main");
        course.setCourseLevel("12");
        course.setCourseName("test main course");
        course.setGenericCourseType("gct");
        course.setLanguage("en");

        when(studentExamRepo.findByPen(traxStudentExamId.getPen())).thenReturn(Arrays.asList(traxStudentExamEntity));
        when(courseService.getCourseDetails(course.getCourseCode(), " ")).thenReturn(course);

        var result = traxStudentExamService.getStudentExamList(traxStudentExamId.getPen(), true);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        TraxStudentExam responseTraxStudentExam = result.get(0);
        assertThat(responseTraxStudentExam.getCourseCode()).isEqualTo(course.getCourseCode());
    }

    @Test
    public void testGetStudentExamList_whenGivenCourseCodeAndLevel_withsortforuifalse_thenReturnSuccess() {
        // ID
    	TraxStudentExamId traxStudentExamId = new TraxStudentExamId();
        traxStudentExamId.setPen("123456789");
        traxStudentExamId.setCourseCode("main");
        traxStudentExamId.setCourseLevel("12");
        traxStudentExamId.setSessionDate("2020-05");

        TraxStudentExamEntity traxStudentExamEntity = new TraxStudentExamEntity();
        traxStudentExamEntity.setCourseKey(traxStudentExamId);

        Course course = new Course();
        course.setCourseCode("main");
        course.setCourseLevel("12");
        course.setCourseName("test main course");
        course.setGenericCourseType("gct");
        course.setLanguage("en");

        when(studentExamRepo.findByPen(traxStudentExamId.getPen())).thenReturn(Arrays.asList(traxStudentExamEntity));
        when(courseService.getCourseDetails(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);

        var result = traxStudentExamService.getStudentExamList(traxStudentExamId.getPen(), false);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        TraxStudentExam responseTraxStudentExam = result.get(0);
        assertThat(responseTraxStudentExam.getCourseCode()).isEqualTo(course.getCourseCode());
    }
}
