package ca.bc.gov.educ.api.course.model.dto.v2;

import ca.bc.gov.educ.api.course.model.dto.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRestriction extends BaseModel {

	private UUID courseRestrictionId;
	@NotBlank(message = "Main Course Code is required and cannot be blank.")
	private String mainCourse;
	@NotBlank(message = "Main Course Level is required and cannot be blank.")
	private String mainCourseLevel;
	@NotBlank(message = "Restricted Course Code is required and cannot be blank.")
	private String restrictedCourse;
	@NotBlank(message = "Restricted Course Level is required and cannot be blank.")
	private String restrictedCourseLevel;
	@Pattern(
			regexp = "^$|^\\d{4}-(0[1-9]|1[0-2])$",
			message = "Restriction Start Date must be in the format yyyy-MM"
	)
	@NotBlank(message = "Restriction Start Date is required and cannot be blank.")
	private String restrictionStartDate;
	@Pattern(
			regexp = "^$|^\\d{4}-(0[1-9]|1[0-2])$",
			message = "Restriction End Date must be in the format yyyy-MM or empty"
	)
	private String restrictionEndDate;

}
