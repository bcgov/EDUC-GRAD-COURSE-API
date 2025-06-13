package ca.bc.gov.educ.api.course.validation.rules.courserestriction;

import ca.bc.gov.educ.api.course.constants.CourseRestrictionValidationIssueTypeCode;
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
@Order(602)
public class InvalidDataRule implements CourseRestrictionValidationBaseRule {

    @Override
    public boolean shouldExecute(CourseRestrictionRuleData courseRestrictionRuleData, List<ValidationIssue> list) {
        return !hasValidationError(list);
    }

    @Override
    public List<ValidationIssue> executeValidation(CourseRestrictionRuleData courseRestrictionRuleData) {
        log.debug("Executing InvalidDataRule :: {}", courseRestrictionRuleData.getCourseRestriction());
        final List<ValidationIssue> validationIssues = new ArrayList<>();
        if(!courseRestrictionRuleData.isUpdate() && courseRestrictionRuleData.getExistingCourseRestriction() != null) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.RESTRICTION_DUPLICATE));
        }
        if(courseRestrictionRuleData.isUpdate() && courseRestrictionRuleData.getExistingCourseRestriction() == null) {
            validationIssues.add(createValidationIssue(CourseRestrictionValidationIssueTypeCode.RESTRICTION_NOT_FOUND));
        }
        return validationIssues;
    }
}
