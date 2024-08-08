package ca.bc.gov.educ.api.course.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Embeddable JPA Entity composite primary key consisting of student number,
 * course code, course level and course session. This class is used as an
 * embedded primary key for the entities which do not have a unique primary key
 * defined by the database view they mirror. The Entities must have these named
 * attributes or have mapped attributes which override these names in order to
 * use this class.
 *
 * @author CGI Information Management Consultants Inc.
 */
@Embeddable
@Data
public class TraxStudentExamId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 10)
    @Column(name = "STUD_NO", insertable = false, updatable = false)
    private String pen;
    @Size(max = 5)
    @Column(name = "CRSE_CODE", insertable = false, updatable = false)
    private String courseCode;
    @Size(max = 3)
    @Column(name = "CRSE_LEVEL", insertable = false, updatable = false)
    private String courseLevel;
    @Size(max = 9)
    @Column(name = "CRSE_SESSION", insertable = false, updatable = false)
    private String sessionDate;

    public TraxStudentExamId() {
    }

    /**
     * Constructor method used by JPA to create a composite primary key.
     *
     * @param studNo
     * @param crseCode
     * @param crseLevel
     * @param crseSession
     */
    public TraxStudentExamId(String studNo, String crseCode, String crseLevel, String crseSession) {
        this.pen = studNo;
        this.courseCode = crseCode;
        this.courseLevel = crseLevel;
        this.sessionDate = crseSession;
    }
}
