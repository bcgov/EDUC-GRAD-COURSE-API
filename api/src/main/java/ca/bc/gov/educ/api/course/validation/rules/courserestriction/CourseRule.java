package ca.bc.gov.educ.api.course.validation.rules.courserestriction;


import ca.bc.gov.educ.api.course.constants.CourseRestrictionValidationIssueTypeCode;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Total no of student course rules: 3
 * CourseRule : Valid Course Check
 * InvalidDataRule : Validates if a course restriction already exists for the given main and restricted courses.
 * RestrictionDateRule : Validates the restriction start and end dates of a course restriction.
 */
@Component
@Slf4j
@Order(601)
public class CourseRule implements CourseRestrictionValidationBaseRule {

    @Override
    public boolean shouldExecute(CourseRestrictionRuleData courseRestrictionRuleData, List<ValidationIssue> list) {
        return !hasValidationError(list);
    }

    @Override
    public List<ValidationIssue> executeValidation(CourseRestrictionRuleData courseRestrictionRuleData) {
        CourseRestriction courseRestriction = courseRestrictionRuleData.getCourseRestriction();
        log.debug("Executing CourseRule :: {}", courseRestriction);
        final List<ValidationIssue> validationIssues = new ArrayList<>();
         Course mainCourse = courseRestrictionRuleData.getMainCourse();
        Course restrictedCourse = courseRestrictionRuleData.getRestrictedCourse();
        if (mainCourse == null) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_INVALID));
        }
        if (restrictedCourse == null) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.RESTRICTED_COURSE_INVALID));
        }
        if(!validationIssues.isEmpty())
            return validationIssues;
        if(mainCourse != null && restrictedCourse != null && mainCourse.getCourseCode().equals(restrictedCourse.getCourseCode()) && mainCourse.getCourseLevel().equals(restrictedCourse.getCourseLevel())) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.MAIN_COURSE_EQUALS_RESTRICTED_COURSE));
        }
        return validationIssues;
    }

}
