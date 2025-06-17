package ca.bc.gov.educ.api.course.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRestrictionValidationIssue extends ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction {
    private boolean hasPersisted;
    private List<ValidationIssue> validationIssues;
}
