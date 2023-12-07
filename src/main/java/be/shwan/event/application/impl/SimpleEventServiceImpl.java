package be.shwan.event.application.impl;

import be.shwan.account.domain.Account;
import be.shwan.event.application.EventService;
import be.shwan.event.domain.Event;
import be.shwan.event.domain.EventRepository;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleEventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public void createEvent(Account account, Study study, EventRequestDto eventRequestDto) {
        if(study.isClosed() || !study.isPublished()) {
            throw new AccessDeniedException("모임을 생성 할 수 없습니다.");
        }

        if (!account.isManagerOf(study) && !account.isMemberOf(study)) {
            throw new AccessDeniedException("모임을 생성 할 수 없습니다.");
        }

        Event event = new Event(account, study, eventRequestDto);
        eventRepository.save(event);
    }
}
