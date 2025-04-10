package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.CourseList;
import ca.bc.gov.educ.api.course.model.dto.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CourseRestrictionServiceTest {

    @Autowired
    private CourseRestrictionService courseRestrictionService;

    @MockBean
    private CourseRestrictionRepository courseRestrictionRepository;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    @Qualifier("default")
    public WebClient webClient;

    @Test
    public void testGetAllCourseRestrictionList() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findAll()).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getAllCourseRestrictionList();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        CourseRestriction responseCourseRestriction = result.get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

    @Test
    public void testGetCourseRestrictions() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findAll()).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictions();
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionList().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictionList().get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());


    }

    @Test
    public void testGetCourseRestrictionsByCourseAndLevel() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevel(eq("MAIN"), eq("12"))).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictions("MAIN", "12");
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionList().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictionList().get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

    @Test
    public void testGetCourseRestrictionsSearchList() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.searchForCourseRestriction(eq(StringUtils.toRootUpperCase(StringUtils.strip("MAIN*", "*"))), eq(StringUtils.toRootUpperCase(StringUtils.strip("1*", "*"))))).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictionsSearchList("MAIN*", "1*");
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

    @Test
    public void testGetCourseRestrictionsListByCourses() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        CourseList courseList = new CourseList();
        courseList.setCourseCodes(Arrays.asList("MAIN"));

        when(courseRestrictionRepository.findByMainCourseIn(courseList.getCourseCodes())).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictionsListByCourses(courseList);
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionList().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictionList().get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

    @Test
    public void testGetCourseRestrictionsByMainCourseAndRestrictedCourse() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findByMainCourseAndRestrictedCourse(eq("MAIN"), eq("REST"))).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictionsByMainCourseAndRestrictedCourse("MAIN", "REST");
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionList().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictionList().get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

    @Test
    public void testGetCourseRestriction() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(
                eq("MAIN"), eq("12"), eq("REST"), eq("12"))).thenReturn(Optional.of(courseRestriction));
        var result = courseRestrictionService.getCourseRestriction("MAIN", "12", "REST", "12");
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(result.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(result.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(result.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(result.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

    @Test
    public void testSaveCourseRestrictionForCreate() {
        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        CourseRestrictionsEntity courseRestrictionEntity = new CourseRestrictionsEntity();
        courseRestrictionEntity.setCourseRestrictionId(UUID.randomUUID());
        courseRestrictionEntity.setMainCourse("MAIN");
        courseRestrictionEntity.setMainCourseLevel("12");
        courseRestrictionEntity.setRestrictedCourse("REST");
        courseRestrictionEntity.setRestrictedCourseLevel("12");
        courseRestrictionEntity.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestrictionEntity.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(
                eq("MAIN"), eq("12"), eq("REST"), eq("12"))).thenReturn(Optional.empty());
        when(courseRestrictionRepository.save(any(CourseRestrictionsEntity.class))).thenReturn(courseRestrictionEntity);

        var result = courseRestrictionService.saveCourseRestriction(courseRestriction);
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionId()).isEqualTo(courseRestrictionEntity.getCourseRestrictionId());
        assertThat(result.getMainCourse()).isEqualTo(courseRestrictionEntity.getMainCourse());
        assertThat(result.getMainCourseLevel()).isEqualTo(courseRestrictionEntity.getMainCourseLevel());
        assertThat(result.getRestrictedCourse()).isEqualTo(courseRestrictionEntity.getRestrictedCourse());
        assertThat(result.getRestrictedCourseLevel()).isEqualTo(courseRestrictionEntity.getRestrictedCourseLevel());
    }

    @Test
    public void testSaveCourseRestrictionForUpdate() {
        CourseRestrictionsEntity courseRestrictionEntity = new CourseRestrictionsEntity();
        courseRestrictionEntity.setCourseRestrictionId(UUID.randomUUID());
        courseRestrictionEntity.setMainCourse("MAIN");
        courseRestrictionEntity.setMainCourseLevel("12");
        courseRestrictionEntity.setRestrictedCourse("REST");
        courseRestrictionEntity.setRestrictedCourseLevel("12");
        courseRestrictionEntity.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestrictionEntity.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setMainCourse("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(
                eq("MAIN"), eq("12"), eq("REST"), eq("12"))).thenReturn(Optional.of(courseRestrictionEntity));
        when(courseRestrictionRepository.save(any(CourseRestrictionsEntity.class))).thenReturn(courseRestrictionEntity);

        var result = courseRestrictionService.saveCourseRestriction(courseRestriction);
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictionId()).isEqualTo(courseRestrictionEntity.getCourseRestrictionId());
        assertThat(result.getMainCourse()).isEqualTo(courseRestrictionEntity.getMainCourse());
        assertThat(result.getMainCourseLevel()).isEqualTo(courseRestrictionEntity.getMainCourseLevel());
        assertThat(result.getRestrictedCourse()).isEqualTo(courseRestrictionEntity.getRestrictedCourse());
        assertThat(result.getRestrictedCourseLevel()).isEqualTo(courseRestrictionEntity.getRestrictedCourseLevel());
    }

}
