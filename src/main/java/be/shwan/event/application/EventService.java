package be.shwan.event.application;

import be.shwan.account.domain.Account;
import be.shwan.event.domain.Event;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.domain.Study;

public interface EventService {
    Event createEvent(Account account, Study study, EventRequestDto eventRequestDto);
}
