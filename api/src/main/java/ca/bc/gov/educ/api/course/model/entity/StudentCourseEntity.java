package ca.bc.gov.educ.api.course.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "STUDENT_COURSE")
public class StudentCourseEntity extends BaseEntity {
    @Id
    @Column(name = "STUDENT_COURSE_ID", nullable = false)
    private UUID id;

    @Column(name = "GRADUATION_STUDENT_RECORD_ID", nullable = false)
    private UUID studentID;

    @Column(name = "COURSE_ID", nullable = false)
    private Integer courseID;

    @Column(name = "COURSE_SESSION", nullable = false)
    private Date sessionDate;

    @Column(name = "INTERIM_PERCENT")
    private Double interimPercent;

    @Column(name = "INTERIM_LETTER_GRADE")
    private String interimLetterGrade;

    @Column(name = "FINAL_PERCENT")
    private Double completedCoursePercentage;

    @Column(name = "FINAL_LETTER_GRADE")
    private String completedCourseLetterGrade;

    @Column(name = "NUMBER_CREDITS")
    private Integer credits;

    @Column(name = "EQUIVALENT_OR_CHALLENGE_CODE")
    private String equivOrChallenge;

    @Column(name = "FINE_ARTS_APPLIED_SKILLS_CODE")
    private String fineArtsAppliedSkills;

    @Column(name = "CUSTOM_COURSE_NAME")
    private String customizedCourseName;

    @Column(name = "STUDENT_COURSE_EXAM_ID")
    private UUID studentExamId;

    @Column(name = "RELATED_COURSE_ID")
    private Integer relatedCourseId;

}
