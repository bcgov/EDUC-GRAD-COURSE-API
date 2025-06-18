package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CourseRestrictionsTransformerTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CourseRestrictionsTransformer courseRestrictionsTransformer;

    @Test
    public void testTransformToDTO() {
        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setCourseRestrictionId(UUID.randomUUID());
        courseRestrictionsEntity.setMainCourse("main");
        courseRestrictionsEntity.setMainCourseLevel("12");
        courseRestrictionsEntity.setRestrictedCourse("rest");
        courseRestrictionsEntity.setRestrictedCourseLevel("12");
        courseRestrictionsEntity.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L).toLocalDate().atStartOfDay());
        courseRestrictionsEntity.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L).toLocalDate().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(courseRestrictionsEntity.getCourseRestrictionId());
        courseRestriction.setMainCourse(courseRestrictionsEntity.getMainCourse());
        courseRestriction.setMainCourseLevel(courseRestrictionsEntity.getMainCourseLevel());
        courseRestriction.setRestrictedCourse(courseRestrictionsEntity.getRestrictedCourse());
        courseRestriction.setRestrictedCourseLevel(courseRestrictionsEntity.getRestrictedCourseLevel());
        courseRestriction.setRestrictionStartDate(courseRestrictionsEntity.getRestrictionStartDate().toString());
        courseRestriction.setRestrictionEndDate(courseRestrictionsEntity.getRestrictionEndDate().toString());

        Mockito.when(modelMapper.map(courseRestrictionsEntity, CourseRestriction.class)).thenReturn(courseRestriction);
        var result = courseRestrictionsTransformer.transformToDTO(courseRestrictionsEntity);
        assertThat(result).isNotNull();
        assertThat(result.getMainCourse()).isEqualTo(courseRestrictionsEntity.getMainCourse());
        assertThat(result.getMainCourseLevel()).isEqualTo(courseRestrictionsEntity.getMainCourseLevel());
        assertThat(result.getRestrictedCourse()).isEqualTo(courseRestrictionsEntity.getRestrictedCourse());
        assertThat(result.getRestrictedCourseLevel()).isEqualTo(courseRestrictionsEntity.getRestrictedCourseLevel());
    }

    @Test
    public void testTransformOptionalToDTO() {
        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setCourseRestrictionId(UUID.randomUUID());
        courseRestrictionsEntity.setMainCourse("main");
        courseRestrictionsEntity.setMainCourseLevel("12");
        courseRestrictionsEntity.setRestrictedCourse("rest");
        courseRestrictionsEntity.setRestrictedCourseLevel("12");
        courseRestrictionsEntity.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L).toLocalDate().atStartOfDay());
        courseRestrictionsEntity.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L).toLocalDate().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(courseRestrictionsEntity.getCourseRestrictionId());
        courseRestriction.setMainCourse(courseRestrictionsEntity.getMainCourse());
        courseRestriction.setMainCourseLevel(courseRestrictionsEntity.getMainCourseLevel());
        courseRestriction.setRestrictedCourse(courseRestrictionsEntity.getRestrictedCourse());
        courseRestriction.setRestrictedCourseLevel(courseRestrictionsEntity.getRestrictedCourseLevel());
        courseRestriction.setRestrictionStartDate(courseRestrictionsEntity.getRestrictionStartDate().toString());
        courseRestriction.setRestrictionEndDate(courseRestrictionsEntity.getRestrictionEndDate().toString());

        Mockito.when(modelMapper.map(courseRestrictionsEntity, CourseRestriction.class)).thenReturn(courseRestriction);
        var result = courseRestrictionsTransformer.transformToDTO(Optional.of(courseRestrictionsEntity));
        assertThat(result).isNotNull();
        assertThat(result.getMainCourse()).isEqualTo(courseRestrictionsEntity.getMainCourse());
        assertThat(result.getMainCourseLevel()).isEqualTo(courseRestrictionsEntity.getMainCourseLevel());
        assertThat(result.getRestrictedCourse()).isEqualTo(courseRestrictionsEntity.getRestrictedCourse());
        assertThat(result.getRestrictedCourseLevel()).isEqualTo(courseRestrictionsEntity.getRestrictedCourseLevel());
    }

    @Test
    public void testTransformToEntity() {
        CourseRestrictionsEntity courseRestrictionsEntity = new CourseRestrictionsEntity();
        courseRestrictionsEntity.setCourseRestrictionId(UUID.randomUUID());
        courseRestrictionsEntity.setMainCourse("main");
        courseRestrictionsEntity.setMainCourseLevel("12");
        courseRestrictionsEntity.setRestrictedCourse("rest");
        courseRestrictionsEntity.setRestrictedCourseLevel("12");
        courseRestrictionsEntity.setRestrictionStartDate(new Date(System.currentTimeMillis() - 10000L).toLocalDate().atStartOfDay());
        courseRestrictionsEntity.setRestrictionEndDate(new Date(System.currentTimeMillis() + 10000L).toLocalDate().atStartOfDay());

        CourseRestriction courseRestriction = new CourseRestriction();
        courseRestriction.setCourseRestrictionId(courseRestrictionsEntity.getCourseRestrictionId());
        courseRestriction.setMainCourse(courseRestrictionsEntity.getMainCourse());
        courseRestriction.setMainCourseLevel(courseRestrictionsEntity.getMainCourseLevel());
        courseRestriction.setRestrictedCourse(courseRestrictionsEntity.getRestrictedCourse());
        courseRestriction.setRestrictedCourseLevel(courseRestrictionsEntity.getRestrictedCourseLevel());
        courseRestriction.setRestrictionStartDate(courseRestrictionsEntity.getRestrictionStartDate().toString());
        courseRestriction.setRestrictionEndDate(courseRestrictionsEntity.getRestrictionEndDate().toString());

        Mockito.when(modelMapper.map(courseRestriction, CourseRestrictionsEntity.class)).thenReturn(courseRestrictionsEntity);
        var result = courseRestrictionsTransformer.transformToEntity(courseRestriction);
        assertThat(result).isNotNull();
        assertThat(result.getMainCourse()).isEqualTo(courseRestriction.getMainCourse());
        assertThat(result.getMainCourseLevel()).isEqualTo(courseRestriction.getMainCourseLevel());
        assertThat(result.getRestrictedCourse()).isEqualTo(courseRestriction.getRestrictedCourse());
        assertThat(result.getRestrictedCourseLevel()).isEqualTo(courseRestriction.getRestrictedCourseLevel());
    }
}
