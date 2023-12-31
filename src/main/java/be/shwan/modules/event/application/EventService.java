package be.shwan.modules.event.application;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.study.domain.Study;

import java.util.List;

public interface EventService {
    Event createEvent(Account account, Study study, EventRequestDto eventRequestDto);

    List<Event> getEventByStudy(Study study);

    List<List<Event>> getEventList(List<Event> events);

    void update(Account account, Study study, Event event, EventRequestDto eventRequestDto);

    void deleteEvent(Event event, Account account);

    Event getEventWithEnrollment(Long id);

    Enrollment enrollEvent(Event event, Account account) throws IllegalAccessException;

    void leaveEvent(Event event, Account account);

    void acceptEnrollment(Event event, Enrollment enrollment);

    void rejectEnrollment(Event event, Enrollment enrollment);

    void checkInEnrollment(Enrollment enrollment);

    void cancelCheckInEnrollment(Enrollment enrollment);

    List<List<Event>> getEventList(Study study);
}
