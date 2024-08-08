package ca.bc.gov.educ.api.course.model.dto;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class TraxStudentExam {
	private String pen;
    private String courseCode;
    private String courseName;
    private String courseLevel;
    private String sessionDate;
    private Double bestSchoolPercentage;
    private String optIn;
    private Double completedSchoolPercentage;   
    private String wroteFlag;    
    private String specialCase;
    private Double completedCourseExamPercentage;    
    private Double completedCourseFinalPercentage;    
    private String completedCourseLetterGrade;    
    private Double bestExamPercentage;
    private Course courseDetails;
    
    public String getCourseCode() {
		return courseCode != null ? courseCode.trim(): null;
	}
	public String getCourseName() {
		return courseName != null ? courseName.trim(): null; 
	}	

	public String getCourseLevel() {
		return courseLevel != null ? courseLevel.trim(): null;
	}
	
	public String getOptIn() {
		return optIn != null ? optIn.trim(): null;
	}
	
	public String getCompletedCourseLetterGrade() {
		return completedCourseLetterGrade != null ? completedCourseLetterGrade.trim(): null;
	}
	
	public Double getCompletedCourseExamPercentage() {
		if(completedCourseExamPercentage == null) {
			return Double.valueOf("0");
		}
		return completedCourseExamPercentage; 
	}
	
	public Double getCompletedCourseFinalPercentage() {
		if(completedCourseFinalPercentage == null) {
			return Double.valueOf("0");
		}
		return completedCourseFinalPercentage; 
	}

	public Double getCompletedSchoolPercentage() {
		if(completedSchoolPercentage == null) {
			return Double.valueOf("0");
		}
		return completedSchoolPercentage; 
	}
	
	public Double getBestSchoolPercentage() {
		if(bestSchoolPercentage == null) {
			return Double.valueOf("0");
		}
		return bestSchoolPercentage; 
	}
	
	public Double getExamSchoolPercentage() {
		if(bestExamPercentage == null) {
			return Double.valueOf("0");
		}
		return bestExamPercentage; 
	}
	
	@Override
	public String toString() {
		return "StudentExam [pen=" + pen + ", courseCode=" + courseCode + ", courseName=" + courseName
				+ ", courseLevel=" + courseLevel + ", sessionDate=" + sessionDate + ", bestSchoolPercentage="
				+ bestSchoolPercentage + ", completedSchoolPercentage=" + completedSchoolPercentage + ", wroteFlag="
				+ wroteFlag + ", specialCase=" + specialCase + ", completedCourseExamPercentage="
				+ completedCourseExamPercentage + ", completedCourseFinalPercentage=" + completedCourseFinalPercentage
				+ ", completedCourseLetterGrade=" + completedCourseLetterGrade + ", bestExamPercentage="
				+ bestExamPercentage + "]";
	}
}