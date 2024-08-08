package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.entity.StudentCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourseEntity, UUID> {
    List<StudentCourseEntity> findByStudentID(UUID studentID);
}
