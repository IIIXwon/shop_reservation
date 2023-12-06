package be.shwan.study.application.impl;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.domain.UserAccount;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.study.dto.StudyRequestDto;
import be.shwan.tag.application.TagService;
import be.shwan.tag.domain.Tag;
import be.shwan.tag.dto.RequestTagDto;
import be.shwan.zone.application.ZoneService;
import be.shwan.zone.domain.Zone;
import be.shwan.zone.dto.RequestZoneDto;
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
    private final TagService tagService;
    private final ZoneService zoneService;

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
        existStudy(study);
        return study;
    }

    @Override
    public Study getStudyToUpdate(String path, Account account) {
        Study study = getStudy(path);
        checkManager(account, study);
        return study;
    }

    @Override
    public Study getStudyToUpdateTag(String path, Account account) {
        Study study = studyRepository.findAccountWithTagByPath(path);
        existStudy(study);
        checkManager(account, study);
        return study;
    }

    @Override
    public Study getStudyToUpdateZone(String path, Account account) {
        Study study = studyRepository.findAccountWithZoneByPath(path);
        existStudy(study);
        checkManager(account, study);
        return study;
    }

    @Override
    public void enableBanner(Study study) {
        study.enableBanner();
    }

    @Override
    public void disableBanner(Study study) {
        study.disableBanner();
    }

    @Override
    public void updateBannerImage(Study study, String image) {
        study.bannerImage(image);
    }

    @Override
    public void addTag(Study study, RequestTagDto tagDto) {
        Tag tag = tagService.getTag(tagDto);
        study.addTag(tag);
    }

    @Override
    public void removeTag(Study study, RequestTagDto tagDto) {
        Tag tag = tagService.findTag(tagDto);
        study.removeTag(tag);
    }

    @Override
    public void addZone(Study study, RequestZoneDto zoneDto) {
        Zone zone = zoneService.findZone(zoneDto.zoneName());
        if (zone == null ) {
            throw new IllegalArgumentException("잘못된 활동 지역 입니다.");
        }
        study.addZone(zone);
    }

    @Override
    public void removeZone(Study study, RequestZoneDto zoneDto) {
        Zone zone = zoneService.findZone(zoneDto.zoneName());
        if (zone == null ) {
            throw new IllegalArgumentException("잘못된 활동 지역 입니다.");
        }
        study.removeZone(zone);
    }

    @Override
    public void updateDescription(Study study, StudyDescriptionRequestDto studyDescriptionRequestDto) {
        study.updateDescription(studyDescriptionRequestDto);
    }

    private void existStudy(Study study) {
        if (study == null) {
            throw new IllegalArgumentException("잘못된 접근입니다");
        }
    }

    private void checkManager(Account account, Study study) {
        if (!account.isManagerOf(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }
}
