package be.shwan.enrollment.application;

import be.shwan.account.domain.Account;
import be.shwan.enrollment.domain.Enrollment;
import be.shwan.event.domain.Event;

public interface EnrollService {
    void enroll(Enrollment enrollment);

    void leaveEnroll(Event event, Account account);

    void updateEnroll(Event event, Account account);
}
