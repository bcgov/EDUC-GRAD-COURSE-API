package ca.bc.gov.educ.api.course.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "EXAMINABLE_COURSE")
public class ExaminableCourseEntity extends BaseEntity {
   
	@Id
	@Column(name = "EXAMINABLE_COURSE_ID", nullable = false)
    private UUID examinableCourseID;

    @Column(name = "COURSE_CODE", nullable = true)
    private String courseCode;

    @Column(name = "COURSE_LEVEL", nullable = true)
    private String courseLevel;

    @Column(name = "COURSE_TITLE", nullable = true)
    private String courseTitle;

    @Column(name = "SCHOOL_WEIGHT_PERCENT", nullable = true)
    private Double schoolWeightPercent;

    @Column(name = "EXAM_WEIGHT_PERCENT", nullable = true)
    private Double examWeightPercent;

    @Column(name = "EXAMINABLE_START", nullable = true)
    private String examinableStart;

    @Column(name = "EXAMINABLE_END", nullable = true)
    private String examinableEnd;

    @Column(name = "OPTIONAL_START", nullable = true)
    private String optionalStart;

    @Column(name = "OPTIONAL_END", nullable = true)
    private String optionalEnd;

}
