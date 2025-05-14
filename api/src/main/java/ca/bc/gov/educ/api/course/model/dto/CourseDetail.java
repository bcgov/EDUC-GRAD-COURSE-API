package ca.bc.gov.educ.api.course.model.dto;

import ca.bc.gov.educ.api.course.model.dto.coreg.CourseAllowableCredits;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseCharacteristics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class CourseDetail extends Course {

    private CourseCharacteristics courseCategory;
    private List<CourseAllowableCredits> courseAllowableCredit;

}
