package be.shwan.modules.notification.domain;

import be.shwan.modules.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);

    void deleteByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountOrderByCreatedDateTime(Account account);
}
