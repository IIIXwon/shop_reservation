package be.shwan.modules.study;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {
    @Autowired
    StudyService studyService;
    @Autowired
    StudyRepository studyRepository;

    public static final String DEFAULT_PATH = "testPath";
    public static final String DEFAULT_TITLE = "testTitle";
    public static final String DEFAULT_SHORT_DESCRIPTION = "testShotDescription";
    public static final String DEFAULT_FULL_DESCRIPTION = "testFullDescription";

    public Study defaultTestCreateStudy(Account manager){
        return createStudy(manager, getDefaultStudyRequestDto());
    }

    public Study createStudy(Account manager, StudyRequestDto requestDto) {
        return studyService.newStudy(manager, requestDto);
    }

    public Study getStudy(String path, Account account) {
        return studyService.getStudy(path, account);
    }

    private StudyRequestDto getDefaultStudyRequestDto() {
        return new StudyRequestDto(DEFAULT_PATH, DEFAULT_TITLE, DEFAULT_SHORT_DESCRIPTION, DEFAULT_FULL_DESCRIPTION);
    }
}
