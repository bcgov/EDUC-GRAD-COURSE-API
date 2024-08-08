package ca.bc.gov.educ.api.course.model.entity;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Data
@Immutable
@Entity
@Table(name = "TRAX_STUDENT_COURSES")
public class TraxStudentCourseEntity {
   
	@EmbeddedId
    private TraxStudentCourseId courseKey;
    
    @Column(name = "FINAL_PCT", nullable = true)
    private Double completedCoursePercentage;

    @Column(name = "FINAL_LG", nullable = true)
    private String completedCourseLetterGrade;
    
    @Column(name = "PRED_PCT", nullable = true)
    private Double interimPercent;

    @Column(name = "PRED_LG", nullable = true)
    private String interimLetterGrade;

    @Column(name = "NUM_CREDITS", nullable = true)
    private Integer credits;
    
    @Column(name = "EQUIV_OR_CHALLENGE", nullable = true)
    private String equivOrChallenge;
    
    @Column(name = "FINE_ARTS_APPLIED_SKILLS", nullable = true)
    private String fineArtsAppliedSkills;    
    
    @Column(name = "RELATED_CRSE", nullable = true)
    private String relatedCourse; 
    
    @Column(name = "RELATED_LEVEL", nullable = true)
    private String relatedLevel;  
    
    @Column(name = "CUSTOMIZED_CRSE_NAME", nullable = true)
    private String customizedCourseName; 
    
    @Column(name = "BEST_SCHOOL_PCT", nullable = true)
    private Double bestSchoolPercent; 
    
    @Column(name = "BEST_EXAM_PCT", nullable = true)
    private Double bestExamPercent;

    @Column(name = "SCHOOL_PCT", nullable = true)
    private Double schoolPercent;

    @Column(name = "EXAM_PCT", nullable = true)
    private Double examPercent;

    @Column(name = "MET_LIT_NUM_REQT", nullable = true)
    private String metLitNumRequirement;
    
    @Column(name = "SPECIAL_CASE", nullable = true)
    private String specialCase; 
    
    @Column(name = "TO_WRITE_FLAG", nullable = true)
    private String toWriteFlag;

}
