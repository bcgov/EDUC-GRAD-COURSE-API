package ca.bc.gov.educ.api.course.model.dto;

import lombok.ToString;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
@Component
public class GradRuleDetails {

	private String ruleCode;
	private String requirementName;
	private String programCode;	
	private String optionalProgramCode;
	private String traxReqNumber;
	
}
