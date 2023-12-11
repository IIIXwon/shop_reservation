package be.shwan.modules.event.domain;

import be.shwan.modules.account.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, EnrollmentRepositoryExtension {
    @EntityGraph(value = "Enrollment.withEventAndAccount", type = EntityGraph.EntityGraphType.FETCH)
    Enrollment findEnrollmentWithEventAndAccountByEventAndAccount(Event event, Account account);

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);


    @EntityGraph(value = "Enrollment.withEventAndStudy", type = EntityGraph.EntityGraphType.LOAD)
    List<Enrollment> findByAccountAndAcceptedOrderByEnrollAtDesc(Account account, boolean accept);
}
