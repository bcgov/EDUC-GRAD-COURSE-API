package ca.bc.gov.educ.api.course.validation.rules;

import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import ca.bc.gov.educ.api.course.validation.rules.courserestriction.CourseRestrictionValidationBaseRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CourseRestrictionRulesProcessor {
    private final List<CourseRestrictionValidationBaseRule> rules;

    @Autowired
    public CourseRestrictionRulesProcessor(final List<CourseRestrictionValidationBaseRule> rules) {
        this.rules = rules;
    }

    public List<ValidationIssue> processRules(CourseRestrictionRuleData courseRestrictionRuleData) {
        final List<ValidationIssue> validationErrorsMap = new ArrayList<>();
        log.debug("Starting validations check for course restriction :: {}", courseRestrictionRuleData.getCourseRestriction());
        rules.forEach(rule -> {
            if(rule.shouldExecute(courseRestrictionRuleData, validationErrorsMap)) {
                validationErrorsMap.addAll(rule.executeValidation(courseRestrictionRuleData));
            }
        });
        return validationErrorsMap;
    }
}
