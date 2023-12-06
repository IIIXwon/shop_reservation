package be.shwan.study.application;

import be.shwan.account.domain.Account;
import be.shwan.study.domain.Study;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.study.dto.StudyRequestDto;
import be.shwan.tag.dto.RequestTagDto;
import be.shwan.zone.dto.RequestZoneDto;
import org.springframework.security.core.Authentication;

public interface StudyService {
    Study newStudy(Account account, StudyRequestDto studyRequestDto);

    void updateDescription(Study study, StudyDescriptionRequestDto studyDescriptionRequestDto);

    Study getStudy(String path);

    Study getStudyToUpdate(String path, Account account);

    void enableBanner(Study study);

    void disableBanner(Study study);

    void updateBannerImage(Study study, String image);

    void addTag(Study study, RequestTagDto tagDto);

    void removeTag(Study study, RequestTagDto tagDto);

    void addZone(Study study, RequestZoneDto zoneDto);

    void removeZone(Study study, RequestZoneDto zoneDto);

    Study getStudyToUpdateTag(String path, Account account);

    Study getStudyToUpdateZone(String path, Account account);
}
