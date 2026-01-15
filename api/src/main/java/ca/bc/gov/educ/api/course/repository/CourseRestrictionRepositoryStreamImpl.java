package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Repository
public class CourseRestrictionRepositoryStreamImpl implements CourseRestrictionRepositoryStream {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Stream all course restrictions
     * @return Stream of CourseRestrictionsEntity
     */
    @Override
    @Transactional(readOnly = true)
    public Stream<CourseRestrictionsEntity> streamAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CourseRestrictionsEntity> cq = cb.createQuery(CourseRestrictionsEntity.class);
        Root<CourseRestrictionsEntity> root = cq.from(CourseRestrictionsEntity.class);

        TypedQuery<CourseRestrictionsEntity> query = entityManager.createQuery(cq);

        return query.getResultStream();
    }
}
