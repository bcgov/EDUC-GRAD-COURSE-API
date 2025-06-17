package ca.bc.gov.educ.api.course.validation.rules.courserestriction;


import ca.bc.gov.educ.api.course.constants.CourseRestrictionValidationIssueTypeCode;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
@Order(603)
public class RestrictionDateRule implements CourseRestrictionValidationBaseRule {

    @Override
    public boolean shouldExecute(CourseRestrictionRuleData courseRestrictionRuleData, List<ValidationIssue> list) {
        return !hasValidationError(list);
    }

    @Override
    public List<ValidationIssue> executeValidation(CourseRestrictionRuleData courseRestrictionRuleData) {
        CourseRestriction courseRestriction = courseRestrictionRuleData.getCourseRestriction();
        log.debug("Executing RestrictionDateRule :: {}", courseRestriction);
        final List<ValidationIssue> validationIssues = new ArrayList<>();
        Course mainCourse = courseRestrictionRuleData.getMainCourse();
        Course restrictedCourse = courseRestrictionRuleData.getRestrictedCourse();
        if (mainCourse != null && restrictedCourse != null) {
            LocalDate restrictionStartLocalDate = StringUtils.isNotBlank(courseRestriction.getRestrictionStartDate()) ? getAsDefaultLocalDate(courseRestriction.getRestrictionStartDate()) : null;
            LocalDate restrictionEndLocalDate = StringUtils.isNotBlank(courseRestriction.getRestrictionEndDate()) ? getAsDefaultLocalDate(courseRestriction.getRestrictionEndDate()) : null;
            validateStartDate(restrictionStartLocalDate, mainCourse, restrictedCourse, validationIssues);
            validateEndDate(restrictionEndLocalDate, mainCourse, restrictedCourse, validationIssues);
            validateRestrictionDateRange(restrictionStartLocalDate, restrictionEndLocalDate, validationIssues);
        }
        return validationIssues;
    }

    private void validateStartDate(LocalDate restrictionStartLocalDate, Course mainCourse, Course restrictedCourse, List<ValidationIssue> validationIssues) {
        if(restrictionStartLocalDate != null && (restrictionStartLocalDate.isBefore(mainCourse.getStartDate().toLocalDate()) || restrictionStartLocalDate.isBefore(restrictedCourse.getStartDate().toLocalDate()))) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.RESTRICTION_START_DATE_INVALID));
        }
    }

    private void validateEndDate(LocalDate restrictionEndLocalDate, Course mainCourse, Course restrictedCourse, List<ValidationIssue> validationIssues) {
        if(restrictionEndLocalDate != null) {
            LocalDate mainCourseCompletionDate = StringUtils.isNotBlank(mainCourse.getCompletionEndDate()) ? getLocalDateFromString(mainCourse.getCompletionEndDate()) : null;
            LocalDate restrictedCourseCompletionDate = StringUtils.isNotBlank(restrictedCourse.getCompletionEndDate())  ? getLocalDateFromString(restrictedCourse.getCompletionEndDate()) : null;

            if((mainCourseCompletionDate != null && restrictionEndLocalDate.isAfter(mainCourseCompletionDate)) ||
                    (restrictedCourseCompletionDate != null && restrictionEndLocalDate.isAfter(restrictedCourseCompletionDate)) ) {
                validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_INVALID));
            }
        }
    }

    private void validateRestrictionDateRange(LocalDate restrictionStartLocalDate, LocalDate restrictionEndLocalDate, List<ValidationIssue> validationIssues) {
        if (restrictionStartLocalDate != null && restrictionEndLocalDate != null && restrictionEndLocalDate.isBefore(restrictionStartLocalDate)) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.RESTRICTION_END_DATE_RANGE_INVALID));
        }
    }

}
