package ca.bc.gov.educ.api.course.model.dto;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class CourseSearchRequest {
    List<String> courseIds;
}
