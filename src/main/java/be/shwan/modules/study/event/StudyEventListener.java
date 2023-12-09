package be.shwan.modules.study.event;

import be.shwan.modules.study.domain.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Async
@Slf4j
@Component
@Transactional(readOnly = true)
public class StudyEventListener {
    @EventListener
    public void studyEventHandlerWithCreate(StudyCreatedEvent studyEvent) {
        Study study = studyEvent.study();
        log.info(study.getTitle() + "is created.");
        // TODO 이메일 보내기, notification db 정보 저장
        throw new RuntimeException();
    }
}
