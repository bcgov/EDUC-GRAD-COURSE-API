package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CourseAlgorithmServiceTest {

    @Autowired
    CourseAlgorithmService courseAlgorithmService;

    @MockBean
    private TraxStudentCourseService traxStudentCourseService;

    @MockBean
    private CourseRequirementService courseRequirementService;

    @MockBean
    private CourseRestrictionService courseRestrictionService;

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

    @Test
    public void testGetCourseAlgorithmData_whenGivenPenNumber_thenReturnSuccess() {

        // Student Course
        TraxStudentCourse traxStudentCourse = new TraxStudentCourse();
        traxStudentCourse.setPen("123456789");
        traxStudentCourse.setCourseCode("MAIN");
        traxStudentCourse.setCourseLevel("12");
        traxStudentCourse.setCourseName("main test course");
        traxStudentCourse.setLanguage("en");

        // Course Requirement Code
        CourseRequirementCodeDTO ruleCode = new CourseRequirementCodeDTO();
        ruleCode.setCourseRequirementCode("RuleCd");
        ruleCode.setDescription("RuleCd Description");
        ruleCode.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        ruleCode.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        // Course Requirement
        CourseRequirement courseRequirement = new CourseRequirement();
        courseRequirement.setCourseRequirementId(UUID.randomUUID());
        courseRequirement.setCourseCode("MAIN");
        courseRequirement.setCourseLevel("12");
        courseRequirement.setRuleCode(ruleCode);

        // Course Restriction
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        // Course Algorithm Data
        CourseAlgorithmData courseAlgorithmData = new CourseAlgorithmData();
        courseAlgorithmData.setStudentCourses(Arrays.asList(traxStudentCourse));
        courseAlgorithmData.setCourseRequirements(Arrays.asList(courseRequirement));
        courseAlgorithmData.setCourseRestrictions(Arrays.asList(courseRestriction));

        CourseList courseList = new CourseList();
        courseList.setCourseCodes(Arrays.asList("MAIN"));

        CourseRequirements courseRequirements = new CourseRequirements();
        courseRequirements.setCourseRequirementList(Arrays.asList(courseRequirement));

        CourseRestrictions courseRestrictions = new CourseRestrictions();
        courseRestrictions.setCourseRestrictionList(Arrays.asList(courseRestriction));

        when(traxStudentCourseService.getStudentCourseList(traxStudentCourse.getPen(), false)).thenReturn(Arrays.asList(traxStudentCourse));
        when(courseRequirementService.getCourseRequirementListByCourses(courseList)).thenReturn(courseRequirements);
        when(courseRestrictionService.getCourseRestrictionsListByCourses(courseList)).thenReturn(courseRestrictions);

        var result = courseAlgorithmService.getCourseAlgorithmData(traxStudentCourse.getPen(), false);

        assertThat(result).isNotNull();
        assertThat(result.getStudentCourses()).isNotEmpty();
        assertThat(result.getCourseRequirements()).isNotEmpty();
        assertThat(result.getCourseRestrictions()).isNotEmpty();
    }
}
