package ca.bc.gov.educ.api.course.controller.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
import ca.bc.gov.educ.api.course.util.GradValidation;
import ca.bc.gov.educ.api.course.util.ResponseHelper;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("ALL")
public class CourseControllerTest {
    @InjectMocks
    private CourseController courseControllerV2;

    @Mock
    private CourseService courseServiceV2;

    @Mock
    ResponseHelper responseHelper;

    @Mock
    GradValidation validation;

    @Test
    public void testGetCourseDetailsByCourseID() {
        // Course
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");
        course.setStartDate(new Date(System.currentTimeMillis() - 10000L));
        course.setEndDate(new Date(System.currentTimeMillis() + 10000L));

        Mockito.when(courseServiceV2.getCourseInfo(course.getCourseID(), "123")).thenReturn(course);
        courseControllerV2.getCourseDetails(course.getCourseID(), "123");
        Mockito.verify(courseServiceV2).getCourseInfo(course.getCourseID(), "123");

    }

    @Test
    public void testGetCourseDetailsByCourseCodeAndCourseLevel() {
        // Course
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");
        course.setStartDate(new Date(System.currentTimeMillis() - 10000L));
        course.setEndDate(new Date(System.currentTimeMillis() + 10000L));

        Mockito.when(courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel(), "123")).thenReturn(course);
        courseControllerV2.getCourseDetails(course.getCourseCode(), course.getCourseLevel(), "123");
        Mockito.verify(courseServiceV2).getCourseInfo(course.getCourseCode(), course.getCourseLevel(), "123");

    }

}
