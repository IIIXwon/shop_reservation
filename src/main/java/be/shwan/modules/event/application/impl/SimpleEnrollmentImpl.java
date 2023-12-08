package be.shwan.modules.event.application.impl;


import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.application.EnrollService;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.EnrollmentRepository;
import be.shwan.modules.event.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SimpleEnrollmentImpl implements EnrollService {
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void enroll(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void updateEnroll(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findEnrollmentWithEventAndAccountByEventAndAccount(event, account);
        enrollment.updateEvent(event);
    }

    @Override
    public void leaveEnroll(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findEnrollmentWithEventAndAccountByEventAndAccount(event, account);
        enrollmentRepository.delete(enrollment);
    }
}
