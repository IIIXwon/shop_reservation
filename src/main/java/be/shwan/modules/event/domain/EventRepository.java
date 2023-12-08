package be.shwan.modules.event.domain;

import be.shwan.modules.study.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {


    @EntityGraph(value = "EventAll.withEnrollments", type = EntityGraph.EntityGraphType.FETCH)
    List<Event> findAllWithEnrollmentsByStudy(Study study);

    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.FETCH)
    Event findEventWithEnrollmentById(Long id);
}
