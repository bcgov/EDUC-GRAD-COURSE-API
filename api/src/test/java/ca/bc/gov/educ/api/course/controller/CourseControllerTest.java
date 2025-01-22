package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.*;
import ca.bc.gov.educ.api.course.service.CourseRequirementService;
import ca.bc.gov.educ.api.course.service.CourseRestrictionService;
import ca.bc.gov.educ.api.course.service.CourseService;
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

import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {
    @Mock
    private CourseService courseService;

    @Mock
    private CourseRequirementService courseRequirementService;

    @Mock
    private CourseRestrictionService courseRestrictionService;

    @InjectMocks
    private CourseController courseController;

    @Mock
    ResponseHelper responseHelper;

    @Mock
    GradValidation validation;

    @Test
    public void testGetAllCourses() {
        // Course
        Course course = new Course();
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");

        Mockito.when(courseService.getCourseList()).thenReturn(Arrays.asList(course));
        courseController.getAllCourses(1,5);
        Mockito.verify(courseService).getCourseList();
    }

    @Test
    public void testGetCourseDetails() {
        // Course
        Course course = new Course();
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");

        Mockito.when(courseService.getCourseDetails("MAIN", "12")).thenReturn(course);
        courseController.getCourseDetails("MAIN", "12");
        Mockito.verify(courseService).getCourseDetails("MAIN", "12");
    }

    @Test
    public void testGetCoursesSearch() {
        // Course
        Course course = new Course();
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");
        course.setStartDate(new Date(System.currentTimeMillis() - 10000L));
        course.setEndDate(new Date(System.currentTimeMillis() + 10000L));

        Mockito.when(courseService.getCourseSearchList("MAIN", "12", "Test1 Name", "EN", course.getStartDate(), course.getEndDate())).thenReturn(Arrays.asList(course));
        courseController.getCoursesSearch("MAIN", "12", "Test1 Name", "EN",  course.getStartDate(), course.getEndDate());
        Mockito.verify(courseService).getCourseSearchList("MAIN", "12", "Test1 Name", "EN",  course.getStartDate(), course.getEndDate());
    }

    @Test
    public void testGetCourseDetailsByCourse() {
        // Course
        Course course = new Course();
        course.setCourseCode("Test");
        course.setCourseLevel("12");
        course.setCourseName("Test1 Name");

        Mockito.when(courseService.getCourseDetails("Test", " ")).thenReturn(course);
        courseController.getCourseDetailsByCourse("Test");
        Mockito.verify(courseService).getCourseDetails("Test", " ");
    }

    @Test
    public void testGetAllCoursesRequirementByRule() {
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

        Mockito.when(courseRequirementService.getAllCourseRequirementListByRule("RuleCd", 1, 5)).thenReturn(Arrays.asList(courseRequirement));
        courseController.getAllCoursesRequirementByRule("RuleCd", 1,5);
        Mockito.verify(courseRequirementService).getAllCourseRequirementListByRule("RuleCd", 1,5);
    }

    @Test
    public void testGetCourseRequirements() {
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

        CourseRequirements courseRequirements = new CourseRequirements();
        courseRequirements.setCourseRequirementList(Arrays.asList(courseRequirement));

        Mockito.when(courseRequirementService.getCourseRequirements()).thenReturn(courseRequirements);
        courseController.getCourseRequirements(null, null);
        Mockito.verify(courseRequirementService).getCourseRequirements();
    }

    @Test
    public void testGetCourseRequirementsByCourseAndLevel() {
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

        CourseRequirements courseRequirements = new CourseRequirements();
        courseRequirements.setCourseRequirementList(Arrays.asList(courseRequirement));

        Mockito.when(courseRequirementService.getCourseRequirements("MAIN", "12")).thenReturn(courseRequirements);
        courseController.getCourseRequirements("MAIN", "12");
        Mockito.verify(courseRequirementService).getCourseRequirements("MAIN", "12");
    }

    @Test
    public void testGetCoursesRequirementByCourse() {
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

        CourseRequirements courseRequirements = new CourseRequirements();
        courseRequirements.setCourseRequirementList(Arrays.asList(courseRequirement));

        CourseList courseList = new CourseList();
        courseList.setCourseCodes(Arrays.asList("MAIN"));

        Mockito.when(courseRequirementService.getCourseRequirementListByCourses(courseList)).thenReturn(courseRequirements);
        courseController.getCoursesRequirementByCourse(courseList);
        Mockito.verify(courseRequirementService).getCourseRequirementListByCourses(courseList);
    }

    @Test
    public void testGetAllCoursesRestriction() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        Mockito.when(courseRestrictionService.getAllCourseRestrictionList()).thenReturn(Arrays.asList(courseRestriction));
        courseController.getAllCoursesRestriction(1,5);
        Mockito.verify(courseRestrictionService).getAllCourseRestrictionList();
    }

    @Test
    public void testGetCourseRestrictionsSearch() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        Mockito.when(courseRestrictionService.getCourseRestrictionsSearchList("MAIN", "12")).thenReturn(Arrays.asList(courseRestriction));
        courseController.getCourseRestrictionsSearch("MAIN", "12");
        Mockito.verify(courseRestrictionService).getCourseRestrictionsSearchList("MAIN", "12");
    }

    @Test
    public void testGetCourseRestrictionsSearch_returnFailure() {
        Mockito.when(validation.hasErrors()).thenReturn(Boolean.TRUE);
        var result = courseController.getCourseRestrictionsSearch("M", "9");
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetCourseRestrictions() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        CourseRestrictions courseRestrictions = new CourseRestrictions();
        courseRestrictions.setCourseRestrictionList(Arrays.asList(courseRestriction));

        Mockito.when(courseRestrictionService.getCourseRestrictions()).thenReturn(courseRestrictions);
        courseController.getCourseRestrictions(null, null);
        Mockito.verify(courseRestrictionService).getCourseRestrictions();
    }

    @Test
    public void testGetCourseRestrictionsByCourseAndLevel() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        CourseRestrictions courseRestrictions = new CourseRestrictions();
        courseRestrictions.setCourseRestrictionList(Arrays.asList(courseRestriction));

        Mockito.when(courseRestrictionService.getCourseRestrictions("MAIN", "12")).thenReturn(courseRestrictions);
        courseController.getCourseRestrictions("MAIN", "12");
        Mockito.verify(courseRestrictionService).getCourseRestrictions("MAIN", "12");
    }

    @Test
    public void testGetCoursesRestrictionsByCourse() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        CourseRestrictions courseRestrictions = new CourseRestrictions();
        courseRestrictions.setCourseRestrictionList(Arrays.asList(courseRestriction));

        CourseList courseList = new CourseList();
        courseList.setCourseCodes(Arrays.asList("MAIN"));

        Mockito.when(courseRestrictionService.getCourseRestrictionsListByCourses(courseList)).thenReturn(courseRestrictions);
        courseController.getCoursesRestrictionsByCourse(courseList);
        Mockito.verify(courseRestrictionService).getCourseRestrictionsListByCourses(courseList);
    }

    @Test
    public void testGetCourseRestriction() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        CourseRestrictions courseRestrictions = new CourseRestrictions();
        courseRestrictions.setCourseRestrictionList(Arrays.asList(courseRestriction));

        Mockito.when(courseRestrictionService.getCourseRestriction("MAIN", "12", "REST", "12")).thenReturn(courseRestriction);
        courseController.getCourseRestriction("MAIN", "12", "REST", "12");
        Mockito.verify(courseRestrictionService).getCourseRestriction("MAIN", "12", "REST", "12");
    }

    @Test
    public void testSaveCourseRestriction() {
        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        CourseRestrictions courseRestrictions = new CourseRestrictions();
        courseRestrictions.setCourseRestrictionList(Arrays.asList(courseRestriction));

        Mockito.when(courseRestrictionService.saveCourseRestriction(courseRestriction)).thenReturn(courseRestriction);
        courseController.saveCourseRestriction(courseRestriction);
        Mockito.verify(courseRestrictionService).saveCourseRestriction(courseRestriction);
    }

    @Test
    public void testCheckCourseRequirementExistsByCourseAndLevelAndRule() {
        String courseCode = "MAIN";
        String courseLevel = "12";
        String ruleCode = "RuleCd";

        Mockito.when(courseRequirementService.checkCourseRequirementExists(courseCode, courseLevel, ruleCode)).thenReturn(true);
        courseController.checkCourseRequirementExists(courseCode, courseLevel, ruleCode);
        Mockito.verify(courseRequirementService).checkCourseRequirementExists(courseCode, courseLevel, ruleCode);
    }

    @Test
    public void checkBlankLanguageCourseByCourseCodeAndCourseLevel() {
        String courseCode = "SMA";
        String courseLevel = "12";
        Mockito.when(courseService.hasBlankLanguageCourse(courseCode, courseLevel)).thenReturn(true);
        courseController.checkBlankLanguageCourse(courseCode, courseLevel);
        Mockito.verify(courseService).hasBlankLanguageCourse(courseCode, courseLevel);
    }

    @Test
    public void checkFrenchLanguageCourseByCourseCodeAndCourseLevel() {
        String courseCode = "SMA";
        String courseLevel = "12";
        Mockito.when(courseService.hasFrenchLanguageCourse(courseCode, courseLevel)).thenReturn(true);
        courseController.checkFrenchLanguageCourse(courseCode, courseLevel);
        Mockito.verify(courseService).hasFrenchLanguageCourse(courseCode, courseLevel);
    }
}
