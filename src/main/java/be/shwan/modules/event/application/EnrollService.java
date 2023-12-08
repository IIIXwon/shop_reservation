package be.shwan.modules.event.application;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.Event;

public interface EnrollService {
    void enroll(Enrollment enrollment);

    void leaveEnroll(Event event, Account account);

    void updateEnroll(Event event, Account account);
}
