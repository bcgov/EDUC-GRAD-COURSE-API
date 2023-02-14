package ca.bc.gov.educ.api.course.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "COURSE_REQUIREMENT_CODE")
public class CourseRequirementCodeEntity extends BaseEntity {
   
	@Id
	@Column(name = "COURSE_REQUIREMENT_CODE", nullable = false)
    private String courseRequirementCode;

    @Column(name = "LABEL", nullable = true)
    private String label;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private Date effectiveDate;

    @Column(name = "EXPIRY_DATE", nullable = true)
    private Date expiryDate;
}
