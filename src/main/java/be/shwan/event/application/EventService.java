package be.shwan.event.application;

import be.shwan.account.domain.Account;
import be.shwan.enrollment.domain.Enrollment;
import be.shwan.event.domain.Event;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.domain.Study;

import java.util.List;

public interface EventService {
    Event createEvent(Account account, Study study, EventRequestDto eventRequestDto);

    List<Event> getEventByStudy(Study study);

    List<List<Event>> getEventList(List<Event> events);

    void update(Account account, Study study, Event event, EventRequestDto eventRequestDto);

    void deleteEvent(Event event, Account account);

    Event getEventWithEnrollment(Long id);

    void enrollEvent(Event event, Account account, Study study);

    void leaveEvent(Event event, Account account, Study study);

    void enrollEvent(Event event, Account account);

    void leaveEvent(Event event, Account account);

    void acceptEnrollment(Event event, Long enrollmentId, Account account);

    void rejectEnrollment(Event event, Long enrollmentId, Account account);

    void acceptEnrollment(Event event, Enrollment enrollment);

    void rejectEnrollment(Event event, Enrollment enrollment);

    void checkInEnrollment(Enrollment enrollment);

    void cancelCheckInEnrollment(Enrollment enrollment);
}
