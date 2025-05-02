package ca.bc.gov.educ.api.course.model.dto.coreg;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Courses implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    private String courseID;

    private String sifSubjectCode;

    private String courseTitle;

    private CourseCharacteristics courseCategory;

    private String programGuideTitle;

    private CourseCharacteristics courseCharacteristics;

    private String externalIndicator;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String completionEndDate;

    private String genericCourseType;

    @Valid
    private List<CourseCode> courseCode;

    @Valid
    private List<CourseAllowableCredits> courseAllowableCredit;

}
