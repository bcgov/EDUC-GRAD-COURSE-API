package ca.bc.gov.educ.api.course.model.dto.coreg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCode  implements Serializable {

    private String courseID;

    private String externalCode;

    private String originatingSystem;

}
