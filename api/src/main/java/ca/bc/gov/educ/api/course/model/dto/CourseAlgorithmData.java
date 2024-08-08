package ca.bc.gov.educ.api.course.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class CourseAlgorithmData {
    List<TraxStudentCourse> traxStudentCours;
    List<CourseRequirement> courseRequirements;
    List<CourseRestriction> courseRestrictions;
}
