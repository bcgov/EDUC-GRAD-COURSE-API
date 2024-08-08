package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.*;
import ca.bc.gov.educ.api.course.service.CourseAlgorithmService;
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
import java.util.Arrays;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CourseAlgorithmControllerTest {
    @Mock
    private CourseAlgorithmService courseAlgorithmService;

    @InjectMocks
    CourseAlgorithmController courseAlgorithmController;

    @Mock
    ResponseHelper responseHelper;

    @Mock
    GradValidation validation;

    @Test
    public void testGetCourseAlgorithmDataByPen() {
        // Student Course
        TraxStudentCourse traxStudentCourse = new TraxStudentCourse();
        traxStudentCourse.setPen("123456789");
        traxStudentCourse.setCourseCode("MAIN");
        traxStudentCourse.setCourseLevel("12");
        traxStudentCourse.setCourseName("main test course");
        traxStudentCourse.setLanguage("en");

        // Course Requirement Code
        CourseRequirementCodeDTO courseRequirementCodeDTO = new CourseRequirementCodeDTO();
        courseRequirementCodeDTO.setCourseRequirementCode("RuleCd");
        courseRequirementCodeDTO.setDescription("RuleCd Description");
        courseRequirementCodeDTO.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeDTO.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        // Course Requirement
        CourseRequirement courseRequirement = new CourseRequirement();
        courseRequirement.setCourseRequirementId(UUID.randomUUID());
        courseRequirement.setCourseCode("MAIN");
        courseRequirement.setCourseLevel("12");
        courseRequirement.setRuleCode(courseRequirementCodeDTO);

        // Course Restriction 
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        // Course Algorithm Data
        CourseAlgorithmData courseAlgorithmData = new CourseAlgorithmData();
        courseAlgorithmData.setTraxStudentCours(Arrays.asList(traxStudentCourse));
        courseAlgorithmData.setCourseRequirements(Arrays.asList(courseRequirement));
        courseAlgorithmData.setCourseRestrictions(Arrays.asList(courseRestriction));

        Mockito.when(courseAlgorithmService.getCourseAlgorithmData(traxStudentCourse.getPen(), false)).thenReturn(courseAlgorithmData);
        courseAlgorithmController.getCourseAlgorithmData(traxStudentCourse.getPen());
        Mockito.verify(courseAlgorithmService).getCourseAlgorithmData(traxStudentCourse.getPen(), false);
    }

}
