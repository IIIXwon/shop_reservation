package be.shwan.event.application.impl;

import be.shwan.account.domain.Account;
import be.shwan.enrollment.application.EnrollService;
import be.shwan.enrollment.domain.Enrollment;
import be.shwan.enrollment.domain.EnrollmentRepository;
import be.shwan.event.application.EventService;
import be.shwan.event.domain.Event;
import be.shwan.event.domain.EventRepository;
import be.shwan.event.domain.EventType;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleEventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EnrollService enrollService;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void enrollEvent(Event event, Account account, Study study) {
        if(validAccount(account, study)) {
            throw new AccessDeniedException("모임에 참가 할 수 없습니다.");
        }

        if(study.updatable()) {
            throw new AccessDeniedException("모임에 참가 할 수 없습니다.");
        }

        Enrollment enrollment = new Enrollment(account, event);
        if (event.getEventType().equals(EventType.FCFS)) {
            event.enroll(enrollment);
            enrollService.enroll(enrollment);
        }

        if (event.getEventType().equals(EventType.CONFIRMATIVE)) {

        }
    }

    @Override
    public void enrollEvent(Event event, Account account) {
        if(!enrollmentRepository.existsByEventAndAccount(event, account)){
            Enrollment enrollment = new Enrollment(account, event);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    @Override
    public void leaveEvent(Event event, Account account, Study study) {
        if(validAccount(account, study)) {
            throw new AccessDeniedException("모임 참가취소를 할 수 없습니다.");
        }

        if(study.updatable()) {
            throw new AccessDeniedException("모임 참가취소를 할 수 없습니다.");
        }

        if (event.getEventType().equals(EventType.FCFS)) {
            event.leaveEnrollByFCFS(event, account);
            enrollService.leaveEnroll(event, account);
        }

        if (event.getEventType().equals(EventType.CONFIRMATIVE)) {

        }
    }

    @Override
    public void leaveEvent(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        event.removeEnrollment(enrollment);
        enrollmentRepository.delete(enrollment);
        event.acceptNextWaitingEnrollment();
    }

    @Override
    public Event createEvent(Account account, Study study, EventRequestDto eventRequestDto) {
        if(study.updatable()) {
            throw new AccessDeniedException("모임을 생성 할 수 없습니다.");
        }

        if (validAccount(account, study)) {
            throw new AccessDeniedException("모임을 생성 할 수 없습니다.");
        }

        Event event = new Event(account, study, eventRequestDto);
        return eventRepository.save(event);
    }

    @Override
    public void update(Account account, Study study, Event event, EventRequestDto eventRequestDto) {
        if(study.updatable()) {
            throw new AccessDeniedException("모임을 수정 할 수 없습니다.");
        }

        if (validAccount(account, study)) {
            throw new AccessDeniedException("모임을 수정 할 수 없습니다.");
        }

        if (!account.isCreateBy(event)) {
            throw new AccessDeniedException("모임을 수정 할 수 없습니다.");
        }

        event.update(eventRequestDto);
        event.acceptNextWaitingEnrollmentList();
    }

    @Override
    public void deleteEvent(Event event, Account account) {
        if (!account.isCreateBy(event)) {
            throw new AccessDeniedException("모임을 삭제 할 수 없습니다.");
        }
        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEventByStudy(Study study) {
        return eventRepository.findAllWithEnrollmentsByStudy(study);
    }

    @Transactional
    @Override
    public Event getEventWithEnrollment(Long id) {
        return eventRepository.findEventWithEnrollmentById(id);
    }

    @Override
    public List<List<Event>> getEventList(List<Event> events) {
        List<List<Event>> eventList = new ArrayList<>();
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        for(Event event : events) {
            if (event.isEndEvent()) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        }
        eventList.add(newEvents);
        eventList.add(oldEvents);
        return eventList;
    }

    private boolean validAccount(Account account, Study study) {
        return !account.isManagerOf(study) && !account.isMemberOf(study);
    }
}
