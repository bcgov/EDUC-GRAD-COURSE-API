package ca.bc.gov.educ.api.course.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDataSourceDTO {
    private Long id;
    private String affectedColumn;
    private String oldValue;
    private String newValue;
}
