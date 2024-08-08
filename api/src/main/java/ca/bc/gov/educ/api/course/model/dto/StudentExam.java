package ca.bc.gov.educ.api.course.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
public class StudentExam {
	private UUID id;
	private Double schoolPercentage;
    private Double bestSchoolPercentage;
	private Double examPercentage;
	private Double bestExamPercentage;
	private String specialCase;
    private String toWriteFlag;

	public Double getSchoolPercentage() {
		if(schoolPercentage == null) {
			return Double.valueOf("0");
		}
		return schoolPercentage;
	}

	public Double getExamPercentage() {
		if(examPercentage == null) {
			return Double.valueOf("0");
		}
		return examPercentage;
	}

	public Double getBestSchoolPercentage() {
		if(bestSchoolPercentage == null) {
			return Double.valueOf("0");
		}
		return bestSchoolPercentage; 
	}
	
	public Double getBestExamPercentage() {
		if(bestExamPercentage == null) {
			return Double.valueOf("0");
		}
		return bestExamPercentage; 
	}
	
	@Override
	public String toString() {
		return "StudentExam [id=" + id + ", schoolPercentage=" + schoolPercentage  + ", bestSchoolPercentage=" + bestSchoolPercentage
				+ ", examPercentage=" + examPercentage  + ", bestExamPercentage=" + bestExamPercentage
				+ ", specialCase=" + specialCase + ", toWriteFlag=" + toWriteFlag + "]";
	}
}