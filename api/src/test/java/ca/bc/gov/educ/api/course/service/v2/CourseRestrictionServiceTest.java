package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.constants.CourseRestrictionValidationIssueTypeCode;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import ca.bc.gov.educ.api.course.service.RESTService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CourseRestrictionServiceTest {

    @Autowired
    CourseRestrictionService courseRestrictionServiceV2;

    @MockBean
    RESTService restService;

    @Autowired
    EducCourseApiConstants constants;

    @MockBean
    public CourseService courseService;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public CourseRestrictionRepository courseRestrictionRepository;

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

    @Mock WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock WebClient.RequestBodySpec requestBodyMock;
    @Mock WebClient.ResponseSpec responseMock;
    @Mock WebClient.RequestBodyUriSpec requestBodyUriMock;

    @Test
    public void testGetCourseRestrictionByRestrictionId_Exist() {
        UUID restrictionId = UUID.randomUUID();
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("01");
        courseRestriction.setRestrictedCourse("RESTRICTED");
        courseRestriction.setRestrictedCourseLevel("01");
        courseRestriction.setCourseRestrictionId(restrictionId);

        CourseRestrictionsEntity courseRestrictionEntity = new CourseRestrictionsEntity();
        courseRestrictionEntity.setMainCourse("MAIN");
        courseRestrictionEntity.setMainCourseLevel("01");
        courseRestrictionEntity.setRestrictedCourse("RESTRICTED");
        courseRestrictionEntity.setRestrictedCourseLevel("01");
        courseRestrictionEntity.setCourseRestrictionId(restrictionId);

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionEntity));
        var result = courseRestrictionServiceV2.getCourseRestrictionById(restrictionId);
        assertThat(result).isNotNull();
    }

    @Test
    public void testGetCourseRestrictionByRestrictionProperties_Exist() {
        UUID restrictionId = UUID.randomUUID();
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("01");
        courseRestriction.setRestrictedCourse("RESTRICTED");
        courseRestriction.setRestrictedCourseLevel("01");
        courseRestriction.setCourseRestrictionId(restrictionId);

        CourseRestrictionsEntity courseRestrictionEntity = new CourseRestrictionsEntity();
        courseRestrictionEntity.setMainCourse("MAIN");
        courseRestrictionEntity.setMainCourseLevel("01");
        courseRestrictionEntity.setRestrictedCourse("RESTRICTED");
        courseRestrictionEntity.setRestrictedCourseLevel("01");
        courseRestrictionEntity.setCourseRestrictionId(restrictionId);

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel("MAIN", "01", "RESTRICTED", "01")).thenReturn(Optional.of(courseRestrictionEntity));
        var result = courseRestrictionServiceV2.getCourseRestriction("MAIN", "01", "RESTRICTED", "01");
        assertThat(result).isNotNull();
    }

    @Test
    public void testCreateCourseRestriction_CourseDoesNotExistError() {
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("01");
        courseRestriction.setRestrictedCourse("RESTRICTED");
        courseRestriction.setRestrictedCourseLevel("01");
        courseRestriction.setRestrictionStartDate("2010-01");
        courseRestriction.setRestrictionEndDate("2000-01");

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(2);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_INVALID.getCode())).findFirst().isPresent());
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTED_COURSE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTED_COURSE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testUpdateCourseRestriction_CourseDoesNotExistError() {
        UUID restrictionId = UUID.randomUUID();
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("01");
        courseRestriction.setRestrictedCourse("RESTRICTED");
        courseRestriction.setRestrictedCourseLevel("01");
        courseRestriction.setRestrictionStartDate("2010-01");
        courseRestriction.setRestrictionEndDate("2000-01");

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(2);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_INVALID.getCode())).findFirst().isPresent());
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTED_COURSE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTED_COURSE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testCreateCourseRestriction_RestrictionEndDateRangeError() {
        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        courseRestriction.setRestrictionEndDate("2000-01");

        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_RANGE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_RANGE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testUpdateCourseRestriction_RestrictionEndDateRangeError() {
        UUID restrictionId = UUID.randomUUID();

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        courseRestriction.setRestrictionEndDate("2000-01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_RANGE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_RANGE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testCreateCourseRestriction_RestrictionStartDateError() {
        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate("2010-01");

        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_START_DATE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_START_DATE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testUpdateCourseRestriction_RestrictionStartDateError() {
        UUID restrictionId = UUID.randomUUID();

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate("2010-01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_START_DATE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_START_DATE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testCreateCourseRestriction_RestrictionEndDateError() {
        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        courseRestriction.setRestrictionEndDate(LocalDate.now().plusYears(5).getYear()+"-"+"01");

        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testUpdateCourseRestriction_RestrictionEndDateError() {
        UUID restrictionId = UUID.randomUUID();

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        courseRestriction.setRestrictionEndDate(LocalDate.now().plusYears(5).getYear()+"-"+"01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_INVALID.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_INVALID.getCode())).findFirst().isPresent());
    }

    @Test
    public void testCreateCourseRestriction_InvalidDataError() {

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        courseRestriction.setRestrictionEndDate(LocalDate.now().plusYears(5).getYear()+"-"+"01");

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_DUPLICATE.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_DUPLICATE.getCode())).findFirst().isPresent());
    }

    @Test
    public void testUpdateCourseRestriction_InvalidDataError() {
        UUID restrictionId = UUID.randomUUID();

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        courseRestriction.setRestrictionEndDate(LocalDate.now().plusYears(5).getYear()+"-"+"01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.empty());
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_NOT_FOUND.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.RESTRICTION_NOT_FOUND.getCode())).findFirst().isPresent());
    }

    @Test
    public void testCreateCourseRestriction_CourseEqualsError() {

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "MAIN";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel)).thenReturn(Optional.empty());
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));
        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_EQUALS_RESTRICTED_COURSE.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_EQUALS_RESTRICTED_COURSE.getCode())).findFirst().isPresent());

    }

    @Test
    public void testUpdateCourseRestriction_CourseEqualsError() {
        UUID restrictionId = UUID.randomUUID();

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "MAIN";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));

        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).hasSize(1);
        assertTrue(result.getValidationIssues().stream().filter(x -> x.getValidationIssueMessage().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_EQUALS_RESTRICTED_COURSE.getMessage()) && x.getValidationFieldName().equals(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_EQUALS_RESTRICTED_COURSE.getCode())).findFirst().isPresent());

    }

    @Test
    public void testCreateCourseRestriction_NoError() {

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel)).thenReturn(Optional.empty());
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));
        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.saveCourseRestriction(courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).isEmpty();
    }

    @Test
    public void testUpdateCourseRestriction_NoError() {
        UUID restrictionId = UUID.randomUUID();

        String mainCourseCode = "MAIN";
        String mainCourseLevel = "01";
        String mainCourseId = "1111";
        String restrictedCourseCode = "RESTRICTED";
        String restrictedCourseLevel = "01";
        String restrictedCourseId = "2222";

        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setMainCourse(mainCourseCode);
        courseRestrictionsEntity.setMainCourseLevel(mainCourseLevel);
        courseRestrictionsEntity.setRestrictedCourse(restrictedCourseCode);
        courseRestrictionsEntity.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestrictionsEntity.setRestrictionStartDate(LocalDate.now().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));
        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).isEmpty();
    }


    private static Course getMainCourse(String courseCode, String courseLevel, String courseId) {
        Course course = new Course();
        course.setCourseID(courseId);
        course.setCourseCode(courseCode);
        course.setCourseLevel(courseLevel);
        course.setStartDate(Date.valueOf(LocalDate.now().minusYears(2)));
        course.setCompletionEndDate(Date.valueOf(LocalDate.now().plusYears(2)).toString());
        return course;
    }



}
