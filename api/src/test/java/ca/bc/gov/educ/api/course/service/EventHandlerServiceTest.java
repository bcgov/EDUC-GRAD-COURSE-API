package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import ca.bc.gov.educ.api.course.util.JsonUtilWithJavaTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EventHandlerServiceTest {

    @Mock
    private StudentCourseRepository studentCourseRepository;

    @Mock
    private TraxStudentCourseService traxStudentCourseService;

    @InjectMocks
    private EventHandlerService eventHandlerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleGetStudentCourseEvent_ReturnsJsonBytes() throws Exception {
        Event event = new Event();
        event.setEventPayload("pen123");

        TraxStudentCourse course1 = new TraxStudentCourse();
        course1.setCourseCode("course1");
        TraxStudentCourse course2 = new TraxStudentCourse();
        course2.setCourseCode("course2");

        List<TraxStudentCourse> fakeStudentCourses = List.of(course1, course2);
        when(traxStudentCourseService.getStudentCourseList("pen123", false))
                .thenReturn(fakeStudentCourses);

        byte[] result = eventHandlerService.handleGetStudentCourseEvent(event);

        assertNotNull(result);
        String json = new String(result);
        assertTrue(json.contains("course1"));
        assertTrue(json.contains("course2"));

        verify(traxStudentCourseService, times(1)).getStudentCourseList("pen123", false);
        verifyNoMoreInteractions(traxStudentCourseService);
    }

    @Test
    void testHandleGetStudentCourseEvent_ThrowsJsonProcessingException() throws Exception {
        Event event = new Event();
        event.setEventPayload("pen123");

        TraxStudentCourse course1 = new TraxStudentCourse();
        course1.setCourseCode("course1");
        TraxStudentCourse course2 = new TraxStudentCourse();
        course2.setCourseCode("course2");

        List<TraxStudentCourse> fakeStudentCourses = List.of(course1, course2);
        when(traxStudentCourseService.getStudentCourseList(anyString(), anyBoolean()))
                .thenReturn(fakeStudentCourses);

        try (MockedStatic<JsonUtilWithJavaTime> mockedJsonUtil = mockStatic(JsonUtilWithJavaTime.class)) {
            mockedJsonUtil.when(() -> JsonUtilWithJavaTime.getJsonBytesFromObject(any()))
                    .thenThrow(JsonProcessingException.class);

            assertThrows(JsonProcessingException.class,
                    () -> eventHandlerService.handleGetStudentCourseEvent(event));
        }

        verify(traxStudentCourseService, times(1)).getStudentCourseList("pen123", false);
    }
}
