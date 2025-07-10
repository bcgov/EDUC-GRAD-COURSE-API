package ca.bc.gov.educ.api.course.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class ExaminableCourse extends BaseModel {

	private UUID examinableCourseID;
	private String programYear;
	private String courseCode;
    private String courseLevel;
	private String courseTitle;
	private Double schoolWeightPercent;
	private Double examWeightPercent;
	private Double schoolWeightPercentPre1989;
	private Double examWeightPercentPre1989;
	private String examinableStart;
	private String examinableEnd;

}