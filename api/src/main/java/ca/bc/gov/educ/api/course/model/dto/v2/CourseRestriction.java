package ca.bc.gov.educ.api.course.model.dto.v2;

import ca.bc.gov.educ.api.course.model.dto.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRestriction extends BaseModel {

	@ReadOnlyProperty
	UUID courseRestrictionId;
	@NotBlank(message = "Main Course Code is required and cannot be blank.")
	String mainCourse;
	@NotBlank(message = "Main Course Level is required and cannot be blank.")
	String mainCourseLevel;
	@NotBlank(message = "Restricted Course Code is required and cannot be blank.")
	String restrictedCourse;
	@NotBlank(message = "Restricted Course Level is required and cannot be blank.")
	String restrictedCourseLevel;
	@Pattern(
			regexp = "^$|^\\d{4}-(0[1-9]|1[0-2])$",
			message = "Restriction Start Date must be in the format yyyy-MM"
	)
	@NotBlank(message = "Restriction Start Date is required and cannot be blank.")
	String restrictionStartDate;
	@Pattern(
			regexp = "^$|^\\d{4}-(0[1-9]|1[0-2])$",
			message = "Restriction End Date must be in the format yyyy-MM or empty"
	)
	String restrictionEndDate;

	public String getRestrictionStartDate() {
		return restrictionStartDate;
	}

	public void setRestrictionStartDate(String restrictionStartDate) {
		this.restrictionStartDate = restrictionStartDate;
	}

	public String getRestrictionEndDate() {
		return restrictionEndDate;
	}

	public void setRestrictionEndDate(String restrictionEndDate) {
		this.restrictionEndDate = restrictionEndDate;
	}
}
