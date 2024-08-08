package ca.bc.gov.educ.api.course.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.course.model.entity.TraxStudentExamEntity;

@Repository
public interface TraxStudentExamRepository extends JpaRepository<TraxStudentExamEntity, UUID> {

    @Query("select c from TraxStudentExamEntity c where c.courseKey.pen=:pen")
    Iterable<TraxStudentExamEntity> findByPen(String pen);

}
