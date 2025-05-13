package ca.bc.gov.educ.api.course.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "EXAMINABLE_COURSE")
public class ExaminableCourseEntity extends BaseEntity {
   
	@Id
	@Column(name = "EXAMINABLE_COURSE_ID", nullable = false)
    private UUID examinableCourseID;

    @Column(name = "COURSE_ID", nullable = false)
    private String courseID;

    @Column(name = "EXAMINABLE_START", nullable = true)
    private Date examinableStart;

    @Column(name = "EXAMINABLE_END", nullable = true)
    private Date examinableEnd;

    @Column(name = "OPTIONAL_START", nullable = true)
    private Date optionalStart;

    @Column(name = "OPTIONAL_END", nullable = true)
    private Date optionalEnd;

}
