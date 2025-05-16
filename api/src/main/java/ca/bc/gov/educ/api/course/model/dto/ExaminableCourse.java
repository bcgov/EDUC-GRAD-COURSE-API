package ca.bc.gov.educ.api.course.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class ExaminableCourse extends BaseModel {

	private UUID examinableCourseID;
	private String courseID;
	private String courseCode;
    private String courseLevel;
	private String schoolWeightPercent;
	private String examWeightPercent;
	private String examinableStart;
	private String examinableEnd;
	private String optionalStart;
    private String optionalEnd;
	private String courseName;

}