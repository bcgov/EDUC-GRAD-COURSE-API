package ca.bc.gov.educ.api.course.model.dto.coreg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoregEventPayload implements Serializable {

    private String id;

    private String affectedTable;

}
