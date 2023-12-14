package be.shwan.modules.study.application.impl;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyDescriptionRequestDto;
import be.shwan.modules.study.dto.StudyPathRequestDto;
import be.shwan.modules.study.dto.StudyRequestDto;
import be.shwan.modules.study.dto.StudyTitleRequestDto;
import be.shwan.modules.study.event.StudyCreatedEvent;
import be.shwan.modules.study.event.StudyUpdatedEvent;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SimpleStudyServiceImpl implements StudyService {
    private final StudyRepository studyRepository;
    private final TagService tagService;
    private final ZoneService zoneService;
    private final ApplicationEventPublisher eventPublisher;
    private final Environment environment;


    @Override
    public Study newStudy(Account account, StudyRequestDto studyRequestDto) {
        Study study = new Study(studyRequestDto.path(), studyRequestDto.title(), studyRequestDto.shortDescription(),
                studyRequestDto.fullDescription());
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    @Transactional(readOnly = true)
    @Override
    public Study getStudy(String path, Account account) {
        Study study = studyRepository.findByPath(path);
        existStudy(study);
        if (!study.isMember(account) && !study.isManager(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return study;
    }

    @Override
    public Study getStudyToUpdate(String path, Account account) {
        Study study = getStudy(path, account);
        checkManager(account, study);
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Study getStudyToUpdateTag(String path, Account account) {
        Study study = studyRepository.findStudyWithTagByPath(path);
        existStudy(study);
        checkManager(account, study);
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Study getStudyToUpdateZone(String path, Account account) {
        Study study = studyRepository.findStudyWithZoneByPath(path);
        existStudy(study);
        checkManager(account, study);
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Study getSimpleStudy(String path, Account account) {
        Study study = studyRepository.findStudyWithManagerByPath(path);
        existStudy(study);
        checkManager(account, study);
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Study getStudyToEnroll(String path, Account account) {
        Study study = studyRepository.findStudyOnlyByPath(path);
        existStudy(study);
        if (!study.isMember(account) && !study.isManager(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return study;
    }

    @Override
    public void publish(Study study) {
        if (study.isPublished()) {
            throw new IllegalStateException("이미 공개중인 스터디 입니다");
        }
        study.publish();
        eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    @Override
    public void closed(Study study) {
        if (study.isClosed() || !study.isPublished()) {
            throw new IllegalStateException("이미 종료된 스터디 입니다");
        }
        study.close();
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디가 종료 되었습니다."));
    }

    @Override
    public void startRecruit(Study study) {
        if (study.isRecruiting()) {
            throw new IllegalStateException("이미 팀원 모집 중입니다.");
        }

        if (study.getRecruitingUpdateDateTime() != null && !LocalDateTime.now().isAfter(study.getRecruitingUpdateDateTime().plusHours(1L))) {
            throw new IllegalStateException("팀원 모집은 1시간에 한번 변경할 수 있습니다.");
        }
        study.startRecruit();
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "팀원 모집이 시작 되었습니다."));
    }

    @Override
    public void stopRecruit(Study study) {
        if (!study.isRecruiting()) {
            throw new IllegalStateException("팀원 모집이 종료되었습니다.");
        }
        ;
        if (!environment.matchesProfiles("test")) {
            if (study.getRecruitingUpdateDateTime() != null && !LocalDateTime.now().isAfter(study.getRecruitingUpdateDateTime().plusHours(1L))) {
                throw new IllegalStateException("팀원 모집은 1시간에 한번 변경할 수 있습니다.");
            }
        }

        study.stopRecruit();
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "팀원 모집이 종료 되었습니다."));
    }

    @Override
    public void updateStudyPath(Study study, StudyPathRequestDto studyPathRequestDto) {
        study.updatePath(studyPathRequestDto.newPath());
    }

    @Override
    public void updateStudyTitle(Study study, StudyTitleRequestDto studyTitleRequestDto) {
        study.updateTitle(studyTitleRequestDto.newTitle());
    }

    @Override
    public void removeStudy(Study study, Account account) {
        if (!study.removeAble() || !study.isManager(account)) {
            throw new IllegalStateException("스터디를 삭제 할 수 없습니다.");

        }
        studyRepository.delete(study);
    }

    @Transactional(readOnly = true)
    @Override
    public Study getStudyWithMembersAndManagers(String path, Account account) {
        Study study = studyRepository.findStudyWithMembersAndManagersByPath(path);
        existStudy(study);
        return study;
    }

    @Override
    public void join(Study study, Account account) {
        checkJoin(account, study);
        study.join(account);
    }

    @Override
    public void leave(Study study, Account account) {
        checkLeave(study, account);
    }

    private void checkLeave(Study study, Account account) {
        if (!study.isMember(account) || study.isManager(account)) {
            throw new AccessDeniedException("스터디에서 탈퇴 할 수 없습니다.");
        }
        study.leave(account);
    }

    private void checkJoin(Account account, Study study) {
        if (study.isMember(account) || study.isManager(account)) {
            throw new AccessDeniedException("스터디에 참가 할 수 없습니다.");
        }
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
        if (zone == null) {
            throw new IllegalArgumentException("잘못된 활동 지역 입니다.");
        }
        study.addZone(zone);
    }

    @Override
    public void removeZone(Study study, RequestZoneDto zoneDto) {
        Zone zone = zoneService.findZone(zoneDto.zoneName());
        if (zone == null) {
            throw new IllegalArgumentException("잘못된 활동 지역 입니다.");
        }
        study.removeZone(zone);
    }

    @Override
    public void updateDescription(Study study, StudyDescriptionRequestDto studyDescriptionRequestDto) {
        study.updateDescription(studyDescriptionRequestDto);
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디가 소개가 변경되었습니다."));
    }

    private void existStudy(Study study) {
        if (study == null) {
            throw new IllegalArgumentException("잘못된 접근입니다");
        }
    }

    private void checkManager(Account account, Study study) {
        if (!study.isManager(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    @Override
    public void generateTestdatas(Account account) {
        for (int i = 0; i < 31; i++) {
            StudyRequestDto requestDto = new StudyRequestDto("test" + i, "테스트 스터디" + i,
                    "테스트용 스터디입니다.", "테스트용 스터디 입니다.");
            Study study = newStudy(account, requestDto);
            study.publish();
            studyRepository.save(study);
        }
    }
}
