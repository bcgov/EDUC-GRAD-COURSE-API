package ca.bc.gov.educ.api.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import ca.bc.gov.educ.api.course.constants.EventType;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import ca.bc.gov.educ.api.course.struct.Event;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class EventHandlerServiceTest {

    private EventHandlerService eventHandlerServiceUnderTest;

    @Mock
    private TraxStudentCourseService traxStudentCourseService;

    @Mock
    private StudentCourseRepository studentCourseRepository;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    @Qualifier("default")
    public WebClient webClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventHandlerServiceUnderTest = new EventHandlerService(studentCourseRepository, traxStudentCourseService);
    }

    static final String PEN = "123456789";

    @Test
    public void testHandleGetStudentCourseEvent_whenNoCourseFound_returnsEmptyList() throws Exception {
        Event event = Event.builder()
                .eventType(EventType.GET_STUDENT_COURSE)
                .sagaId(UUID.randomUUID())
                .eventPayload(PEN)
                .build();

        when(traxStudentCourseService.getStudentCourseList(PEN, false)).thenReturn(Collections.emptyList());

        byte[] responseBytes = eventHandlerServiceUnderTest.handleGetStudentCourseEvent(event);
        List<?> courses = new ObjectMapper().readValue(responseBytes, new TypeReference<List<Object>>() {});
        assertThat(courses).isEmpty();
    }

    @Test
    public void testHandleGetStudentCourseEvent_whenCoursesFound_returnsCourseData() throws Exception {
        Event event = Event.builder()
                .eventType(EventType.GET_STUDENT_COURSE)
                .sagaId(UUID.randomUUID())
                .eventPayload(PEN)
                .build();

        List<TraxStudentCourse> dummyCourses = List.of(
                new TraxStudentCourse(
                        "131411258", "CLE", "CAREER-LIFE EDUCATION", 4, "", "2021/06", "", null, 100.0, "A", 100.0, "", null, null, null, null, "", "", null, 4, null, "", null, "", "N", "", "", " ", null, null, "N", false, false, false,
                        new Course(
                                "CLE", "", "CAREER-LIFE EDUCATION", "", Date.valueOf("2018-06-30"), Date.valueOf("1858-11-16"), "", "3201860", 4
                        )
                ),
                new TraxStudentCourse(
                        "131411258", "CLC", "CAREER-LIFE CONNECTIONS", 4, "", "2023/06", "", null, 95.0, "A", 95.0, "", null, null, null, null, "", "", null, 4, null, "", null, "", "N", "", "", " ", null, null, "N", false, false, false,
                        new Course(
                                "CLC", "", "CAREER-LIFE CONNECTIONS", "", Date.valueOf("2018-06-30"), Date.valueOf("1858-11-16"), "", "3201862", 4
                        )
                )
        );

        when(traxStudentCourseService.getStudentCourseList(PEN, false)).thenReturn(dummyCourses);

        byte[] responseBytes = eventHandlerServiceUnderTest.handleGetStudentCourseEvent(event);
        List<TraxStudentCourse> courses = new ObjectMapper().readValue(responseBytes, new TypeReference<>() {});
        assertThat(courses).hasSize(2);
        assertThat(courses.get(0).getCourseCode()).isEqualTo("CLE");
    }

    @Test
    public void testHandleGetStudentCourseEvent_whenExceptionThrown_propagatesException() {
        Event event = Event.builder()
                .eventType(EventType.GET_STUDENT_COURSE)
                .sagaId(UUID.randomUUID())
                .eventPayload(PEN)
                .build();

        when(traxStudentCourseService.getStudentCourseList(PEN, false))
                .thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> eventHandlerServiceUnderTest.handleGetStudentCourseEvent(event));
        assertThat(exception.getMessage()).isEqualTo("Test exception");
    }
}
