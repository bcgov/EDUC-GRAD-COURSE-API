package ca.bc.gov.educ.api.course.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRegistryEventDTO {
    private Long id;
    private String affectedTable;
    private Long affectedId;
    private Long registryEventTypeCharId;
    private LocalDateTime createdDate;
    private String createdUser;
    private List<CourseDataSourceDTO> dataSources;
}
