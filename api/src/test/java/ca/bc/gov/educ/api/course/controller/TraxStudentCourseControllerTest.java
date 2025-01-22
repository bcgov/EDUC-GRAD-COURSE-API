package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.service.TraxStudentCourseService;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class TraxStudentCourseControllerTest {

    @Mock
    private TraxStudentCourseService traxStudentCourseService;

    @InjectMocks
    StudentCourseController studentCourseController;

    @Mock
    ResponseHelper responseHelper;

    @Mock
    GradValidation validation;

    @Test
    public void testGetStudentCoursesByPEN() {
        TraxStudentCourse traxStudentCourse = new TraxStudentCourse();
        traxStudentCourse.setPen("123456789");
        traxStudentCourse.setCourseCode("main");
        traxStudentCourse.setCourseLevel("12");
        traxStudentCourse.setCourseName("main test course");
        traxStudentCourse.setLanguage("en");

        Mockito.when(traxStudentCourseService.getStudentCourseList(traxStudentCourse.getPen(), true)).thenReturn(Arrays.asList(traxStudentCourse));
        studentCourseController.getStudentCourseByPEN(traxStudentCourse.getPen(), true);
        Mockito.verify(traxStudentCourseService).getStudentCourseList(traxStudentCourse.getPen(), true);
    }

    @Test
    public void testValidationError() {
        Mockito.when(validation.hasErrors()).thenReturn(true);
        var result = studentCourseController.getStudentCourseByPEN("", true);
        Mockito.verify(validation).hasErrors();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void checkFrenchImmersionCourse() {
        String pen = "123456789";
        Mockito.when(traxStudentCourseService.checkFrenchImmersionCourse(pen)).thenReturn(true);
        studentCourseController.checkFrenchImmersionCourse(pen);
        Mockito.verify(traxStudentCourseService).checkFrenchImmersionCourse(pen);
    }

    @Test
    public void checkFrenchImmersionCourseByPenAndCourseLevel() {
        String pen = "123456789";
        String courseLevel = "11";
        Mockito.when(traxStudentCourseService.checkFrenchImmersionCourse(pen, courseLevel)).thenReturn(true);
        studentCourseController.checkFrenchImmersionCourse(pen, courseLevel);
        Mockito.verify(traxStudentCourseService).checkFrenchImmersionCourse(pen, courseLevel);
    }

    @Test
    public void checkFrenchImmersionCourseFor1986ENByPenAndCourseLevel() {
        String pen = "123456789";
        String courseLevel = "11";
        Mockito.when(traxStudentCourseService.checkFrenchImmersionCourseForEN(pen, courseLevel)).thenReturn(true);
        studentCourseController.checkFrenchImmersionCourseForEN(pen, courseLevel);
        Mockito.verify(traxStudentCourseService).checkFrenchImmersionCourseForEN(pen, courseLevel);
    }

}
