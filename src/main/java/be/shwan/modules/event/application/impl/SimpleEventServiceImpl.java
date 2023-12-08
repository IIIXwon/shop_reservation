package be.shwan.modules.event.application.impl;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.application.EnrollService;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.EnrollmentRepository;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.domain.EventRepository;
import be.shwan.modules.event.domain.EventType;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.study.domain.Study;
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

        if (!event.getCreateBy().equals(account)) {
            throw new AccessDeniedException("모임을 수정 할 수 없습니다.");
        }

        event.update(eventRequestDto);
        event.acceptNextWaitingEnrollmentList();
    }

    @Override
    public void deleteEvent(Event event, Account account) {
        if (!event.getCreateBy().equals(account)) {
            throw new AccessDeniedException("모임을 삭제 할 수 없습니다.");
        }

        eventRepository.delete(event);
    }

    @Override
    public void acceptEnrollment(Event event, Long enrollmentId, Account account) {
        if(event.isAbleToAcceptEnrollment()) {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
            enrollment.accept();
        }
    }

    @Override
    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
    }

    @Override
    public void rejectEnrollment(Event event, Long enrollmentId, Account account) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        if(event.isAbleToRejectEnrollment(enrollment)) {
            enrollment.reject();
        }
    }

    @Override
    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
    }

    @Override
    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.attend();
    }

    @Override
    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.cancelAttend();
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
        return !study.isManager(account) && !study.isMember(account);
    }
}
