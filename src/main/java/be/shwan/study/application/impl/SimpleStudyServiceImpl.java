package be.shwan.study.application.impl;

import be.shwan.account.domain.Account;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
