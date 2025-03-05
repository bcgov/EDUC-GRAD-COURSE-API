package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.StatusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface Student event repository.
 */
public interface StatusEventRepository extends JpaRepository<StatusEvent, UUID> {
    /**
     * Find by saga id optional.
     *
     * @param sagaId the saga id
     * @return the optional
     */
    Optional<StatusEvent> findBySagaId(UUID sagaId);

    /**
     * Find by saga id and event type optional.
     *
     * @param sagaId    the saga id
     * @param eventType the event type
     * @return the optional
     */
    Optional<StatusEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);

    /**
     * Find by event status list.
     *
     * @param eventStatus the event status
     * @return the list
     */
    List<StatusEvent> findByEventStatus(String eventStatus);

    @Transactional
    @Modifying
    @Query("delete from StatusEvent where createDate <= :createDate")
    void deleteByCreateDateBefore(LocalDateTime createDate);
}
