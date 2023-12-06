package be.shwan.study.application;

import be.shwan.account.domain.Account;
import be.shwan.study.domain.Study;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.study.dto.StudyRequestDto;
import org.springframework.security.core.Authentication;

public interface StudyService {
    Study newStudy(Account account, StudyRequestDto studyRequestDto);

    void updateDescription(Study study, StudyDescriptionRequestDto studyDescriptionRequestDto);

    Study getStudy(String path);

    Study getStudyToUpdate(String path, Account account);
}
