package be.shwan.modules.event;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.domain.EventRepository;
import be.shwan.modules.event.domain.EventType;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventFactory {
    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    public static final String DEFAULT_EVENT_TITLE = "test";
    public static final EventType DEFAULT_EVENT_TYPE = EventType.FCFS;
    public static final int DEFAULT_EVENT_LIMIT_OF_ENROLLMENTS = 2;
    public static final LocalDateTime DEFAULT_EVENT_END_ENROLLMENT_DATE_TIME = LocalDateTime.now().plusDays(1L);
    public static final LocalDateTime DEFAULT_EVENT_START_DATE_TIME = LocalDateTime.now().plusDays(2L);
    public static final LocalDateTime DEFAULT_EVENT_END_DATE_TIME = LocalDateTime.now().plusMonths(1L);
    public static final String DEFAULT_EVENT_DESCRIPTION = "";

    public Event createDefaultEvent(Account account, Study study) {
        return createEvent(account, study, getDefaultEventRequestDto());
    }

    public Event createEvent(Account account, Study study, EventRequestDto requestDto) {
        return eventService.createEvent(account, study, requestDto);
    }

    private EventRequestDto getDefaultEventRequestDto() {
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter();
        return new EventRequestDto(DEFAULT_EVENT_TITLE, DEFAULT_EVENT_TYPE, DEFAULT_EVENT_LIMIT_OF_ENROLLMENTS,
                DEFAULT_EVENT_END_ENROLLMENT_DATE_TIME.format(dateTimeFormatter),
                DEFAULT_EVENT_START_DATE_TIME.format(dateTimeFormatter),
                DEFAULT_EVENT_END_DATE_TIME.format(dateTimeFormatter), DEFAULT_EVENT_DESCRIPTION);
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }


    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow();
    }
}
