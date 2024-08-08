package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.entity.StudentExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExamEntity, UUID> {
}
