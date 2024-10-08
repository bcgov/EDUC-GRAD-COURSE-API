package ca.bc.gov.educ.api.course.model.dto.coreg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCharacteristics implements Serializable {

    private String id;

    private String type;

    private String code;

    private String description;

}
