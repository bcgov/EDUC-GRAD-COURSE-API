package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.CourseList;
import ca.bc.gov.educ.api.course.model.dto.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void testGetAllCourseRestrictionList() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourseLevel("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findAll()).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getAllCourseRestrictionList();
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
    public void testGetCourseRestrictions() {
        CourseRestrictionsEntity courseRestriction = new CourseRestrictionsEntity();
        courseRestriction.setCourseRestrictionId(UUID.randomUUID());
        courseRestriction.setMainCourseLevel("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findAll()).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictions();
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictions().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictions().get(0);
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
        courseRestriction.setMainCourseLevel("MAIN");
        courseRestriction.setMainCourseLevel("12");
        courseRestriction.setRestrictedCourse("REST");
        courseRestriction.setRestrictedCourseLevel("12");
        courseRestriction.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L));
        courseRestriction.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L));

        when(courseRestrictionRepository.findByMainCourseAndMainCourseLevel(eq("MAIN"), eq("12"))).thenReturn(Arrays.asList(courseRestriction));
        var result = courseRestrictionService.getCourseRestrictions("MAIN", "12");
        assertThat(result).isNotNull();
        assertThat(result.getCourseRestrictions().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictions().get(0);
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
        courseRestriction.setMainCourseLevel("MAIN");
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
        courseRestriction.setMainCourseLevel("MAIN");
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
        assertThat(result.getCourseRestrictions().size()).isEqualTo(1);
        CourseRestriction responseCourseRestriction = result.getCourseRestrictions().get(0);
        assertThat(responseCourseRestriction.getCourseRestrictionId()).isEqualTo(courseRestriction.getCourseRestrictionId());
        assertThat(responseCourseRestriction.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(responseCourseRestriction.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(responseCourseRestriction.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(responseCourseRestriction.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }

}
