package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.*;
import ca.bc.gov.educ.api.course.model.entity.CourseRequirementCodeEntity;
import ca.bc.gov.educ.api.course.model.entity.CourseRequirementEntity;
import ca.bc.gov.educ.api.course.repository.CourseRequirementCodeRepository;
import ca.bc.gov.educ.api.course.repository.CourseRequirementRepository;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CourseRequirementServiceTest {

    @Autowired
    EducCourseApiConstants constants;

    @Autowired
    private CourseRequirementService courseRequirementService;

    @MockBean
    private CourseRequirementRepository courseRequirementRepository;

    @MockBean
    private CourseRequirementCodeRepository courseRequirementCodeRepository;

    @MockBean
    private CourseService courseService;

    @MockBean
    private RESTService restService;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    public WebClient webClient;

    @Before
    public void setUp() {
        openMocks(this);
    }

    @After
    public void tearDown() {
        //Placeholder method
    }

    @Test
    public void testGetAllCourseRequirementList() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        Course course = new Course();
        course.setCourseCode("MAIN");
        course.setCourseLevel("12");
        course.setCourseName("MAIN Course");

        GradRuleDetails ruleDetails = new GradRuleDetails();
        ruleDetails.setRuleCode("RuleCd");
        ruleDetails.setProgramCode("2018-EN");
        ruleDetails.setRequirementName("Test");

        Page<CourseRequirementEntity> pagedResult = new PageImpl<>(Arrays.asList(courseRequirementEntity));

        when(courseRequirementRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);
        when(courseService.getCourseDetails("MAIN", "12")).thenReturn(course);


        when(restService.get(String.format(constants.getRuleDetailProgramManagementApiUrl(), courseRequirementEntity.getRuleCode().getCourseRequirementCode()), List.class)).thenReturn(Arrays.asList(ruleDetails));

        var result = courseRequirementService.getAllCourseRequirementList(1,5);
        assertThat(result).isNotNull().hasSize(1);
        AllCourseRequirements allCourseRequirements = result.get(0);
        assertThat(allCourseRequirements.getCourseName()).isEqualTo(course.getCourseName());
        assertThat(allCourseRequirements.getRequirementName()).isEqualTo(ruleDetails.getRequirementName());
    }

    @Test
    public void testGetAllCourseRequirementListByRule() {
        String ruleCodeValue = "RuleCd";

        CourseRequirementCodeDTO ruleCode = new CourseRequirementCodeDTO();
        ruleCode.setCourseRequirementCode(ruleCodeValue);
        ruleCode.setDescription("RuleCd Description");
        ruleCode.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        ruleCode.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode(ruleCodeValue);
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        Course course = new Course();
        course.setCourseCode("MAIN");
        course.setCourseLevel("12");
        course.setCourseName("MAIN Course");

        GradRuleDetails ruleDetails = new GradRuleDetails();
        ruleDetails.setRuleCode(ruleCodeValue);
        ruleDetails.setProgramCode("2018-EN");
        ruleDetails.setRequirementName("Test");

        Page<CourseRequirementEntity> pagedResult = new PageImpl<>(Arrays.asList(courseRequirementEntity));

        when(courseRequirementCodeRepository.findById(ruleCodeValue)).thenReturn(Optional.of(courseRequirementCodeEntity));
        when(courseRequirementRepository.findByRuleCode(eq(courseRequirementCodeEntity), any(Pageable.class))).thenReturn(pagedResult);
        when(courseService.getCourseDetails("MAIN", "12")).thenReturn(course);

        var result = courseRequirementService.getAllCourseRequirementListByRule(ruleCodeValue, 1, 5);

        assertThat(result).isNotNull().hasSize(1);
        CourseRequirement responseCourseRequirement = result.get(0);
        assertThat(responseCourseRequirement.getCourseRequirementId()).isEqualTo(courseRequirementEntity.getCourseRequirementId());
        assertThat(responseCourseRequirement.getCourseCode()).isEqualTo(courseRequirementEntity.getCourseCode());
        assertThat(responseCourseRequirement.getCourseName()).isEqualTo(course.getCourseName());
    }

    @Test
    public void testGetCourseRequirements() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        when(courseRequirementRepository.findAll()).thenReturn(Arrays.asList(courseRequirementEntity));
        var result = courseRequirementService.getCourseRequirements();
        assertThat(result).isNotNull();
        assertThat(result.getCourseRequirementList()).hasSize(1);
        CourseRequirement courseRequirement = result.getCourseRequirementList().get(0);
        assertThat(courseRequirement.getCourseRequirementId()).isEqualTo(courseRequirementEntity.getCourseRequirementId());
    }

    @Test
    public void testGetCourseRequirementsByCourseAndLevel() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        when(courseRequirementRepository.findByCourseCodeAndCourseLevel("MAIN", "12")).thenReturn(Arrays.asList(courseRequirementEntity));
        var result = courseRequirementService.getCourseRequirements("MAIN", "12");
        assertThat(result).isNotNull();
        assertThat(result.getCourseRequirementList()).hasSize(1);
        CourseRequirement courseRequirement = result.getCourseRequirementList().get(0);
        assertThat(courseRequirement.getCourseRequirementId()).isEqualTo(courseRequirementEntity.getCourseRequirementId());
    }

    @Test
    public void testCheckCourseRequirementExistsByCourseAndLevelAndRule() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        when(courseRequirementRepository.countByCourseCodeAndCourseLevelAndRuleCode("MAIN", "12", "RuleCd")).thenReturn(1L);
        var result = courseRequirementService.checkCourseRequirementExists("MAIN", "12", "RuleCd");
        assertThat(result).isTrue();
    }

    @Test
    public void testCheckCourseRequirementExistsByCourseAndLevelAndRule_whenCourseLevel_isBlank() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel(" ");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        when(courseRequirementRepository.countByCourseCodeAndCourseLevelAndRuleCode("MAIN", " ", "RuleCd")).thenReturn(1L);
        var result = courseRequirementService.checkCourseRequirementExists("MAIN", " ", "RuleCd");
        assertThat(result).isTrue();
    }

    @Test
    public void testSaveCourseRequirementExistsByCourseAndLevelAndRule() {
        CourseRequirementCodeDTO courseRequirementCode = new CourseRequirementCodeDTO();
        courseRequirementCode.setCourseRequirementCode("RuleCd");
        courseRequirementCode.setDescription("RuleCd Description");
        courseRequirementCode.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCode.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirement courseRequirement = new CourseRequirement();
        courseRequirement.setCourseCode("MAIN");
        courseRequirement.setCourseLevel("12");
        courseRequirement.setRuleCode(courseRequirementCode);

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        CourseRequirementEntity responseCourseRequirementEntity = new CourseRequirementEntity();
        responseCourseRequirementEntity.setCourseRequirementId(courseRequirementEntity.getCourseRequirementId());
        responseCourseRequirementEntity.setCourseCode("MAIN");
        responseCourseRequirementEntity.setCourseLevel("12");
        responseCourseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        when(courseRequirementRepository.findByCourseCodeAndCourseLevelAndRuleCode("MAIN", "12", "RuleCd")).thenReturn(Arrays.asList(courseRequirementEntity));
        when(courseRequirementCodeRepository.findById("RuleCd")).thenReturn(Optional.of(courseRequirementCodeEntity));
        when(courseRequirementRepository.save(courseRequirementEntity)).thenReturn(responseCourseRequirementEntity);
        var result = courseRequirementService.saveCourseRequirement(courseRequirement);
        assertThat(result).isNotNull();
        assertThat(result.getCourseCode()).isEqualTo("MAIN");
        assertThat(result.getCourseLevel()).isEqualTo("12");
        assertThat(result.getRuleCode()).isNotNull();
        assertThat(result.getRuleCode().getCourseRequirementCode()).isEqualTo("RuleCd");
    }

    @Test
    public void testSaveCourseRequirementExistsByCourseAndLevelAndRule_whenCourseLevel_isBlank() {
        CourseRequirementCodeDTO courseRequirementCode = new CourseRequirementCodeDTO();
        courseRequirementCode.setCourseRequirementCode("RuleCd");
        courseRequirementCode.setDescription("RuleCd Description");
        courseRequirementCode.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCode.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirement courseRequirement = new CourseRequirement();
        courseRequirement.setCourseCode("MAIN");
        courseRequirement.setCourseLevel(" ");
        courseRequirement.setRuleCode(courseRequirementCode);

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel(" ");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        CourseRequirementEntity responseCourseRequirementEntity = new CourseRequirementEntity();
        responseCourseRequirementEntity.setCourseRequirementId(courseRequirementEntity.getCourseRequirementId());
        responseCourseRequirementEntity.setCourseCode("MAIN");
        responseCourseRequirementEntity.setCourseLevel(" ");
        responseCourseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        when(courseRequirementRepository.findByCourseCodeAndCourseLevelAndRuleCode("MAIN", " ", "RuleCd")).thenReturn(Arrays.asList(courseRequirementEntity));
        when(courseRequirementCodeRepository.findById("RuleCd")).thenReturn(Optional.of(courseRequirementCodeEntity));
        when(courseRequirementRepository.save(courseRequirementEntity)).thenReturn(responseCourseRequirementEntity);
        var result = courseRequirementService.saveCourseRequirement(courseRequirement);
        assertThat(result).isNotNull();
        assertThat(result.getCourseCode()).isEqualTo("MAIN");
        assertThat(result.getCourseLevel()).isEmpty();
        assertThat(result.getRuleCode()).isNotNull();
        assertThat(result.getRuleCode().getCourseRequirementCode()).isEqualTo("RuleCd");
    }

    @Test
    public void testGetCourseRequirementListByCourses() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        CourseList courseList = new CourseList();
        courseList.setCourseCodes(Arrays.asList("MAIN"));

        when(courseRequirementRepository.findByCourseCodeIn(courseList.getCourseCodes())).thenReturn(Arrays.asList(courseRequirementEntity));
        var result = courseRequirementService.getCourseRequirementListByCourses(courseList);
        assertThat(result).isNotNull();
        assertThat(result.getCourseRequirementList()).hasSize(1);
        CourseRequirement courseRequirement = result.getCourseRequirementList().get(0);
        assertThat(courseRequirement.getCourseRequirementId()).isEqualTo(courseRequirementEntity.getCourseRequirementId());
    }

    @Test
    public void testGetCourseRequirementSearchList() {
        CourseRequirementCodeEntity courseRequirementCodeEntity = new CourseRequirementCodeEntity();
        courseRequirementCodeEntity.setCourseRequirementCode("RuleCd");
        courseRequirementCodeEntity.setDescription("RuleCd Description");
        courseRequirementCodeEntity.setEffectiveDate(new Date(System.currentTimeMillis() - 10000L));
        courseRequirementCodeEntity.setExpiryDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRequirementEntity courseRequirementEntity = new CourseRequirementEntity();
        courseRequirementEntity.setCourseRequirementId(UUID.randomUUID());
        courseRequirementEntity.setCourseCode("MAIN");
        courseRequirementEntity.setCourseLevel("12");
        courseRequirementEntity.setRuleCode(courseRequirementCodeEntity);

        Course course = new Course();
        course.setCourseCode("MAIN");
        course.setCourseLevel("12");
        course.setCourseName("MAIN Course");

        GradRuleDetails ruleDetails = new GradRuleDetails();
        ruleDetails.setRuleCode("RuleCd");
        ruleDetails.setProgramCode("2018-EN");
        ruleDetails.setRequirementName("Test");

        when(courseRequirementRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(courseRequirementEntity));
        when(courseService.getCourseDetails("MAIN", "12")).thenReturn(course);
        when(restService.get(String.format(constants.getRuleDetailProgramManagementApiUrl(), courseRequirementEntity.getRuleCode().getCourseRequirementCode()), List.class)).thenReturn(Arrays.asList(ruleDetails));

        var result = courseRequirementService.getCourseRequirementSearchList("MAIN", "12", "RuleCd");
        assertThat(result).isNotNull().hasSize(1);
        AllCourseRequirements allCourseRequirements = result.get(0);
        assertThat(allCourseRequirements.getCourseName()).isEqualTo(course.getCourseName());
        assertThat(allCourseRequirements.getRequirementName()).isEqualTo(ruleDetails.getRequirementName());
    }

}
