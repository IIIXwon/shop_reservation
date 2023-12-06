package be.shwan.study.application.impl;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.domain.UserAccount;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.study.dto.StudyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SimpleStudyServiceImpl implements StudyService {
    private final StudyRepository studyRepository;

    @Override
    public Study newStudy(Account account, StudyRequestDto studyRequestDto) {
        Study study = new Study(studyRequestDto.path(), studyRequestDto.title(), studyRequestDto.shortDescription(),
                studyRequestDto.fullDescription());
        study.addManager(account);
        return studyRepository.save(study);
    }

    @Override
    public Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if (study == null) {
            throw new IllegalArgumentException("잘못된 접근입니다");
        }
        return study;
    }

    @Override
    public Study getStudyToUpdate(String path, Account account) {
        Study study = getStudy(path);
        if (!account.isManagerOf(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return study;
    }

    @Override
    public void updateDescription(Study study, StudyDescriptionRequestDto studyDescriptionRequestDto) {
        study.updateDescription(studyDescriptionRequestDto);
    }
}
