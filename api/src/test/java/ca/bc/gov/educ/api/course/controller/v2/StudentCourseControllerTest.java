package ca.bc.gov.educ.api.course.controller.v2;

import ca.bc.gov.educ.api.course.model.dto.StudentCourse;
import ca.bc.gov.educ.api.course.service.StudentCourseService;
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
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("ALL")
public class StudentCourseControllerTest {

    @InjectMocks
    private StudentCourseController studentCourseControllerV2;

    @Mock
    private StudentCourseService studentCourseService;

    @Mock
    ResponseHelper responseHelper;

    @Mock
    GradValidation validation;

    @Test
    public void testGetStudentCoursesByStudentID() {
        UUID studentID = UUID.randomUUID();

        StudentCourse sc1 = new StudentCourse();
        sc1.setStudentID(studentID);
        sc1.setCourseID("1234567");
        sc1.setCourseCode("Test");
        sc1.setCourseLevel("12");
        sc1.setOriginalCredits(4);

        StudentCourse sc2 = new StudentCourse();
        sc2.setStudentID(studentID);
        sc2.setCourseID("7654321");
        sc2.setCourseCode("QA");
        sc2.setCourseLevel("12");
        sc2.setOriginalCredits(3);

        Mockito.when(studentCourseService.getStudentCourses(studentID, true)).thenReturn(Arrays.asList(sc1, sc2));
        studentCourseControllerV2.getStudentCoursesByStudentID(studentID, true);
        Mockito.verify(studentCourseService).getStudentCourses(studentID, true);
    }

    @Test
    public void testSaveStudentCourse() {
        UUID studentID = UUID.randomUUID();

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(studentID);
        sc.setCourseID("1234567");
        sc.setCourseCode("Test");
        sc.setCourseLevel("12");
        sc.setOriginalCredits(4);

        Mockito.when(studentCourseService.saveStudentCourse(sc)).thenReturn(sc);
        studentCourseControllerV2.saveStudentCourse(sc);
        Mockito.verify(studentCourseService).saveStudentCourse(sc);
    }

    @Test
    public void testSaveStudentCourse_when_courseID_is_notProvided() {
        UUID studentID = UUID.randomUUID();

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(studentID);
        sc.setCourseCode("Test");
        sc.setCourseLevel("12");
        sc.setOriginalCredits(4);

        Mockito.when(validation.hasErrors()).thenReturn(true);
        var response = studentCourseControllerV2.saveStudentCourse(sc);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testDeleteStudentCourse() {
        UUID studentCourseId = UUID.randomUUID();
        Mockito.when(studentCourseService.deleteStudentCourse(studentCourseId)).thenReturn(1);
        studentCourseControllerV2.deleteStudentCourse(studentCourseId);
        Mockito.verify(studentCourseService).deleteStudentCourse(studentCourseId);
    }

    @Test
    public void testDeleteStudentCourse_when_studentCourse_is_notFound() {
        UUID studentCourseId = UUID.randomUUID();
        Mockito.when(studentCourseService.deleteStudentCourse(studentCourseId)).thenReturn(0);
        studentCourseControllerV2.deleteStudentCourse(studentCourseId);
        Mockito.verify(studentCourseService).deleteStudentCourse(studentCourseId);
    }

    @Test
    public void testDeleteStudentCourse_when_studentCourseId_is_notProvided() {
        Mockito.when(validation.hasErrors()).thenReturn(true);
        var response = studentCourseControllerV2.deleteStudentCourse(null);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
