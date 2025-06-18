package ca.bc.gov.educ.api.course.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRestrictionRuleData {
    private CourseRestriction courseRestriction;
    private CourseRestriction existingCourseRestriction;
    private Course mainCourse;
    private Course restrictedCourse;
    private boolean isUpdate;
}
