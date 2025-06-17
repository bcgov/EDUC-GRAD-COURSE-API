package ca.bc.gov.educ.api.course.validation.rules.courserestriction;

import ca.bc.gov.educ.api.course.constants.CourseRestrictionValidationIssueTypeCode;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import ca.bc.gov.educ.api.course.validation.rules.ValidationBaseRule;
import io.micrometer.common.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public interface CourseRestrictionValidationBaseRule extends ValidationBaseRule<CourseRestrictionRuleData, ValidationIssue> {

    default ValidationIssue createValidationIssue(CourseRestrictionValidationIssueTypeCode fieldCode){
        ValidationIssue validationIssue = new ValidationIssue();
        validationIssue.setValidationFieldName(fieldCode.getCode());
        validationIssue.setValidationIssueSeverityCode(fieldCode.getSeverityCode().getCode());
        validationIssue.setValidationIssueMessage(fieldCode.getMessage());
        return validationIssue;
    }

    //This supports format YYYY-MM
    default LocalDate getAsDefaultLocalDate(String dateValue) {
        if(StringUtils.isNotBlank(dateValue) && dateValue.length() == 7) {
            return getLocalDateFromString(dateValue+"-01");
        }
        return null;
    }

    default LocalDate getLocalDateFromString(String localDate) {
        return LocalDate.parse(localDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    default boolean hasValidationError(List<ValidationIssue> validationIssues) {
        return validationIssues.stream().anyMatch(issue -> "ERROR".equals(issue.getValidationIssueSeverityCode()));
    }

}
