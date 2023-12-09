package be.shwan.modules.event.application.impl;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.application.EnrollService;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.*;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.event.event.EnrollmentEvent;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.event.StudyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Enrollment enrollEvent(Event event, Account account) throws IllegalAccessException {
        if(!enrollmentRepository.existsByEventAndAccount(event, account)){
            Enrollment enrollment = new Enrollment(account, event);
            event.addEnrollment(enrollment);
            return enrollmentRepository.save(enrollment);
        }
        throw new IllegalAccessException("잘못된 접근입니다");
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
        Event savedEvent = eventRepository.save(event);
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디 모임이 생성되었습니다."));
        return savedEvent;
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
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디 모임이 변경 되었습니다."));
    }

    @Override
    public void deleteEvent(Event event, Account account) {
        if (!event.getCreateBy().equals(account)) {
            throw new AccessDeniedException("모임을 삭제 할 수 없습니다.");
        }

        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(), "스터디 모임이 취소 되었습니다."));
    }

    @Override
    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentEvent(enrollment, event.getTitle() + "에 참가 신청 완료되었습니다."));
    }

    @Override
    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentEvent(enrollment, event.getTitle() + "에 참가 신청이 거절되었습니다."));
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
