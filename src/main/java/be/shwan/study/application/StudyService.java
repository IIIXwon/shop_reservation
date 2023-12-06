package be.shwan.study.application;

import be.shwan.account.domain.Account;
import be.shwan.study.domain.Study;
import be.shwan.study.dto.StudyRequestDto;

public interface StudyService {
    Study newStudy(Account account, StudyRequestDto studyRequestDto);
}
