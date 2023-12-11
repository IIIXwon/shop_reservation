package be.shwan.modules.event.domain;

import be.shwan.modules.account.domain.Account;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EnrollmentRepositoryExtension {
    List<Enrollment> findAcceptedEnrollmentByAccount(Account account);
}
