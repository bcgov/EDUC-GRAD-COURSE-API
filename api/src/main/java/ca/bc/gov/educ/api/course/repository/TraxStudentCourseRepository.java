package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.entity.TraxStudentCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TraxStudentCourseRepository extends JpaRepository<TraxStudentCourseEntity, UUID> {

    @Query("select c from TraxStudentCourseEntity c where c.courseKey.pen=:pen")
    Iterable<TraxStudentCourseEntity> findByPen(String pen);

    @Query(value="select count(*) from STUD_XCRSE sx, COURSE_REQUIREMENT cr\n" +
            "where sx.stud_no = :pen \n" +
            "and trim(sx.crse_code) = cr.course_code\n" +
            "and trim(sx.crse_level) = cr.course_level\n" +
            "and cr.course_requirement_code = 202\n", nativeQuery=true)
    long countFrenchImmersionCourses(@Param("pen") String pen);

    @Query(value="select count(*) from TRAX_STUDENT_COURSES tsc, COURSE_REQUIREMENT cr \n" +
            "where tsc.pen = :pen \n" +
            "and trim(tsc.crse_code) = cr.course_code \n" +
            "and trim(tsc.crse_level) = cr.course_level \n" +
            "and trim(tsc.crse_code) in ('FRAL', 'FRALP') \n" +
            "and trim(tsc.crse_level) = :level ", nativeQuery=true)
    long countFrenchImmersionCourses(@Param("pen") String pen, @Param("level") String level);

    @Query(value="select count(*) from TRAX_STUDENT_COURSES tsc, COURSE_REQUIREMENT cr \n" +
            "where tsc.pen = :pen \n" +
            "and trim(tsc.crse_code) = cr.course_code \n" +
            "and trim(tsc.crse_level) = cr.course_level \n" +
            "and trim(tsc.crse_code) = 'FRAL' \n" +
            "and trim(tsc.crse_level) = :level ", nativeQuery=true)
    long countFrenchImmersionCourse(@Param("pen") String pen, @Param("level") String level);

}
