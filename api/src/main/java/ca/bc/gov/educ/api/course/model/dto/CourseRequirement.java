package ca.bc.gov.educ.api.course.model.dto;

import java.sql.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class CourseRequirement extends BaseModel {

	private UUID courseRequirementId;
	private String courseCode;
    private String courseLevel;
    private CourseRequirementCodeDTO ruleCode;
    private String courseName;
    private Date startDate;
    private Date endDate;
    private String completionEndDate;

    public String getCourseCode() {
        return courseCode != null ? courseCode.trim() : null;
    }

    public String getCourseLevel() {
        return courseLevel != null ? courseLevel.trim() : null;
    }
    
    public String getCourseName() {
        return courseName != null ? courseName.trim() : null;
    }
}
