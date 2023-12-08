package be.shwan.enrollment.domain;

import be.shwan.account.domain.Account;
import be.shwan.event.domain.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @EntityGraph(value = "Enrollment.withEventAndAccount", type = EntityGraph.EntityGraphType.FETCH)
    Enrollment findEnrollmentWithEventAndAccountByEventAndAccount(Event event, Account account);

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
