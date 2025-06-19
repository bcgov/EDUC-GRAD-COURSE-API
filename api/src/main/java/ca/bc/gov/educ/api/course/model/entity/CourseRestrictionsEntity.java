package ca.bc.gov.educ.api.course.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "COURSE_RESTRICTION")
public class CourseRestrictionsEntity extends BaseEntity  {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
			name = "UUID",
			strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "COURSE_RESTRICTION_ID", nullable = false)
    private UUID courseRestrictionId;

	@Column(name = "MAIN_COURSE", nullable = false)
    private String mainCourse;  
	
	@Column(name = "MAIN_COURSE_LEVEL", nullable = true)
    private String mainCourseLevel;
	
	@Column(name = "RESTRICTED_COURSE", nullable = false)
    private String restrictedCourse; 
	
	@Column(name = "RESTRICTED_COURSE_LVL", nullable = true)
    private String restrictedCourseLevel;

	@Column(name = "RESTRICTION_EFFECTIVE_DATE", nullable = false)
    private LocalDateTime restrictionStartDate;
	
	@Column(name = "RESTRICTION_EXPIRY_DATE", nullable = true)
    private LocalDateTime restrictionEndDate;

	public UUID getCourseRestrictionId() { return courseRestrictionId; }

	public void setCourseRestrictionId(UUID courseRestrictionId) { this.courseRestrictionId = courseRestrictionId; }

	public String getMainCourse() { return mainCourse; }

	public void setMainCourse(String mainCourse) { this.mainCourse = mainCourse; }

	public String getMainCourseLevel() { return mainCourseLevel; }

	public void setMainCourseLevel(String mainCourseLevel) { this.mainCourseLevel = mainCourseLevel; }

	public String getRestrictedCourse() { return restrictedCourse; }

	public void setRestrictedCourse(String restrictedCourse) { this.restrictedCourse = restrictedCourse; }

	public String getRestrictedCourseLevel() { return restrictedCourseLevel; }

	public void setRestrictedCourseLevel(String restrictedCourseLevel) { this.restrictedCourseLevel = restrictedCourseLevel; }

	public LocalDateTime getRestrictionStartDate() { return restrictionStartDate; }

	public void setRestrictionStartDate(LocalDateTime restrictionStartDate) { this.restrictionStartDate = restrictionStartDate; }

	public LocalDateTime getRestrictionEndDate() { return restrictionEndDate; }

	public void setRestrictionEndDate(LocalDateTime restrictionEndDate) { this.restrictionEndDate = restrictionEndDate; }
	
}
