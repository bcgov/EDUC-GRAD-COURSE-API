package ca.bc.gov.educ.api.course.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Course {

	private String courseCode;
    private String courseLevel;
    private String courseName;
    private String language;    
    private Date startDate;
    private Date endDate;
    private String genericCourseType;
    private String courseID;
	private Integer numCredits;

	public Course(String cle, String s, String s1, String s2, Date date, Date date1, String s3, String number, int i) {
	}

	public String getCourseCode() {
		return courseCode != null ? courseCode.trim(): null;
	}
	public String getCourseName() {
		return courseName != null ? courseName.trim(): null; 
	}	

	public String getCourseLevel() {
		return courseLevel != null ? courseLevel.trim(): null;
	}
	
	public String getLanguage() {
		return language != null ? language.trim(): null;
	}
	
	public String getGenericCourseType() {
		return genericCourseType != null ? genericCourseType.trim(): null;
	}
    
	@Override
	public String toString() {
		return "Course [courseID=" + courseID + ", courseCode=" + courseCode + ", courseLevel=" + courseLevel +
				", courseName=" + courseName + ", language=" + language + ", startDate=" + startDate + ", endDate="
				+ endDate + ", genericCourseType=" + genericCourseType + "]";
	}		
}
