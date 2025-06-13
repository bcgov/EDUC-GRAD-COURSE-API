package ca.bc.gov.educ.api.course.controller.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseDetail;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.CourseSearchRequest;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.service.v2.CourseRestrictionService;
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
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("ALL")
public class CourseControllerTest {
    @InjectMocks
    private CourseController courseControllerV2;

    @Mock
    private CourseService courseServiceV2;

    @Mock
    private CourseRestrictionService courseRestrictionServiceV2;

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

        Mockito.when(courseServiceV2.getCourseInfo(course.getCourseID())).thenReturn(course);
        courseControllerV2.getCourseDetails(course.getCourseID());
        Mockito.verify(courseServiceV2).getCourseInfo(course.getCourseID());

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

        Mockito.when(courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);
        courseControllerV2.getCourseDetails(course.getCourseCode(), course.getCourseLevel());
        Mockito.verify(courseServiceV2).getCourseInfo(course.getCourseCode(), course.getCourseLevel());

    }

    @Test
    public void testGetCourseDetailsByCourseIDs() {
        // Course
        CourseDetail course = new CourseDetail();
        course.setCourseID("1234567");
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");
        course.setStartDate(new Date(System.currentTimeMillis() - 10000L));
        course.setEndDate(new Date(System.currentTimeMillis() + 10000L));
        CourseSearchRequest courseSearchRequest= new CourseSearchRequest();
        courseSearchRequest.setCourseIds(List.of(course.getCourseID()));
        Mockito.when(courseServiceV2.getCourseDetails(courseSearchRequest)).thenReturn(List.of(course));
        courseControllerV2.getCourseDetails(courseSearchRequest);
        Mockito.verify(courseServiceV2).getCourseDetails(courseSearchRequest);

    }

    @Test
    public void testCreateCourseRestriction() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("01");
        courseRestriction.setRestrictedCourse("RESTRICTED");
        courseRestriction.setRestrictedCourseLevel("01");
        courseRestriction.setRestrictionStartDate("2010-01");
        courseRestriction.setRestrictionEndDate("2000-01");

        CourseRestrictionValidationIssue validationIssue = new CourseRestrictionValidationIssue();
        validationIssue.setHasPersisted(true);
        validationIssue.setCourseRestrictionId(UUID.randomUUID());
        ResponseEntity<CourseRestrictionValidationIssue> expectedResponse = ResponseEntity.ok(validationIssue);
        when(responseHelper.GET(validationIssue)).thenReturn(expectedResponse);

        when(courseRestrictionServiceV2.saveCourseRestriction(courseRestriction)).thenReturn(validationIssue);
        ResponseEntity<CourseRestrictionValidationIssue> actual = courseControllerV2.saveCourseRestriction(courseRestriction);
        assertThat(actual).isEqualTo(expectedResponse);

    }


    @Test
    public void testUpdateCourseRestriction() {
        // Course Restriction
        UUID courseRestrictionId = UUID.randomUUID();
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("01");
        courseRestriction.setRestrictedCourse("RESTRICTED");
        courseRestriction.setRestrictedCourseLevel("01");
        courseRestriction.setRestrictionStartDate("2010-01");
        courseRestriction.setRestrictionEndDate("2000-01");

        CourseRestrictionValidationIssue validationIssue = new CourseRestrictionValidationIssue();
        validationIssue.setHasPersisted(true);
        validationIssue.setCourseRestrictionId(UUID.randomUUID());
        ResponseEntity<CourseRestrictionValidationIssue> expectedResponse = ResponseEntity.ok(validationIssue);
        when(responseHelper.GET(validationIssue)).thenReturn(expectedResponse);

        when(courseRestrictionServiceV2.updateCourseRestriction(courseRestrictionId, courseRestriction)).thenReturn(validationIssue);
        ResponseEntity<CourseRestrictionValidationIssue> actual = courseControllerV2.updateCourseRestriction(courseRestrictionId, courseRestriction);
        assertThat(actual).isEqualTo(expectedResponse);

    }

}
