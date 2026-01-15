package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.constants.CourseRestrictionValidationIssueTypeCode;
import ca.bc.gov.educ.api.course.model.dto.CourseDetail;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.mapper.CourseRestrictionMapper;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepositoryStream;
import ca.bc.gov.educ.api.course.service.RESTService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
    public CourseRestrictionRepositoryStream courseRestrictionRepositoryStream;

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

    @MockBean
    public CourseRestrictionMapper courseRestrictionMapper;

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
        when(courseRestrictionMapper.toStructure(courseRestrictionEntity)).thenReturn(courseRestriction);
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
        when(courseRestrictionMapper.toStructure(courseRestrictionEntity)).thenReturn(courseRestriction);
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
        when(courseRestrictionMapper.toStructure(courseRestrictionsEntity)).thenReturn(courseRestriction);
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
        when(courseRestrictionMapper.toStructure(courseRestrictionsEntity)).thenReturn(courseRestriction);
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
        when(courseRestrictionMapper.toStructure(courseRestrictionsEntity)).thenReturn(courseRestriction);
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
        when(courseRestrictionMapper.toStructure(courseRestrictionsEntity)).thenReturn(courseRestriction);
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
        courseRestrictionsEntity.setCreateDate(new java.util.Date());
        courseRestrictionsEntity.setUpdateDate(new java.util.Date());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");
        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel)).thenReturn(Optional.empty());
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));
        when(courseRestrictionRepository.saveAndFlush(courseRestrictionMapper.toEntity(courseRestriction))).thenReturn(courseRestrictionsEntity);
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
        courseRestrictionsEntity.setCreateDate(new java.util.Date());
        courseRestrictionsEntity.setUpdateDate(new java.util.Date());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse(mainCourseCode);
        courseRestriction.setMainCourseLevel(mainCourseLevel);
        courseRestriction.setRestrictedCourse(restrictedCourseCode);
        courseRestriction.setRestrictedCourseLevel(restrictedCourseLevel);
        courseRestriction.setRestrictionStartDate(LocalDate.now().getYear()+"-"+"01");

        when(courseRestrictionRepository.findById(restrictionId)).thenReturn(Optional.of(courseRestrictionsEntity));
        when(courseService.getCourseInfo(mainCourseCode, mainCourseLevel)).thenReturn(getMainCourse(mainCourseCode, mainCourseLevel, mainCourseId));
        when(courseService.getCourseInfo(restrictedCourseCode, restrictedCourseLevel)).thenReturn(getMainCourse(restrictedCourseCode, restrictedCourseLevel, restrictedCourseId));
        when(courseRestrictionRepository.saveAndFlush(courseRestrictionMapper.toEntity(courseRestriction))).thenReturn(courseRestrictionsEntity);
        when(courseRestrictionMapper.toStructure(courseRestrictionsEntity)).thenReturn(courseRestriction);
        CourseRestrictionValidationIssue result = courseRestrictionServiceV2.updateCourseRestriction(restrictionId, courseRestriction);
        assertNotNull(result);
        assertThat(result.getValidationIssues()).isEmpty();
    }

    @Test
    public void testGenerateCourseRestrictionsReportStream() throws IOException {
        CourseRestrictionsEntity restriction1 = new CourseRestrictionsEntity();
        restriction1.setCourseRestrictionId(UUID.randomUUID());
        restriction1.setMainCourse("MAIN");
        restriction1.setMainCourseLevel("12");
        restriction1.setRestrictedCourse("RESTRICTED");
        restriction1.setRestrictedCourseLevel("11");
        restriction1.setRestrictionStartDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        restriction1.setRestrictionEndDate(LocalDateTime.of(2025, 12, 31, 0, 0));

        CourseRestrictionsEntity restriction2 = new CourseRestrictionsEntity();
        restriction2.setCourseRestrictionId(UUID.randomUUID());
        restriction2.setMainCourse("TEST");
        restriction2.setMainCourseLevel("10");
        restriction2.setRestrictedCourse("BLOCK");
        restriction2.setRestrictedCourseLevel("09");
        restriction2.setRestrictionStartDate(LocalDateTime.of(2021, 6, 1, 0, 0));
        restriction2.setRestrictionEndDate(LocalDateTime.of(2099, 12, 31, 0, 0));

        List<CourseRestrictionsEntity> restrictions = List.of(restriction1, restriction2);

        when(courseRestrictionRepositoryStream.streamAll()).thenReturn(restrictions.stream());

        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // for mocking
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        courseRestrictionServiceV2.generateCourseRestrictionsReportStream(response);

        verify(response).setContentType("text/csv");
        verify(response).setHeader(eq("Content-Disposition"), contains("CourseRestrictions_"));

        String csvContent = outputStream.toString();
        assertThat(csvContent).isNotEmpty();
        assertThat(csvContent).contains("Course Code Main");
        assertThat(csvContent).contains("Course Level Main");
        assertThat(csvContent).contains("MAIN");
        assertThat(csvContent).contains("12");
        assertThat(csvContent).contains("RESTRICTED");
        assertThat(csvContent).contains("11");
        assertThat(csvContent).contains("2020-01-01");
        assertThat(csvContent).contains("2025-12-31");
        assertThat(csvContent).contains("TEST");
        assertThat(csvContent).contains("10");

        verify(courseRestrictionRepositoryStream, times(1)).streamAll();
    }


    private static CourseDetail getMainCourse(String courseCode, String courseLevel, String courseId) {
        CourseDetail course = new CourseDetail();
        course.setCourseID(courseId);
        course.setCourseCode(courseCode);
        course.setCourseLevel(courseLevel);
        course.setStartDate(Date.valueOf(LocalDate.now().minusYears(2)));
        course.setCompletionEndDate(Date.valueOf(LocalDate.now().plusYears(2)).toString());
        return course;
    }



}
