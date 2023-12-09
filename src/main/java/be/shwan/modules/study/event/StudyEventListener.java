package be.shwan.modules.study.event;

import be.shwan.infra.config.AppProperties;
import be.shwan.infra.mail.application.EmailService;
import be.shwan.infra.mail.dto.EmailMessage;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountPredicates;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationType;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Async
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    @EventListener
    public void studyEventHandlerWithCreate(StudyCreatedEvent studyEvent) {
        Study study =studyRepository.findStudyWithTagsAndZonesById(studyEvent.study().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendStudyCreatedEmail(account, study);
            }

            if(account.isStudyCreatedByWeb()) {
                studyCreatedNotification(account, study);
            }
        });
    }

    private void studyCreatedNotification(Account account, Study study) {
        String title = study.getTitle();
        String link = "/study/" + study.getEncodePath();
        String message = study.getShortDescription();
        Notification notification = new Notification(title, link, message, account, NotificationType.STUDY_CREATED);
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedEmail(Account account, Study study) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", "새로운 스터디가 생겼습니다.");
        context.setVariable("linkName", study.getTitle());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", "/study/" + study.getEncodePath());
        templateEngine.process("mail/simple-link", context);
        String to = account.getEmail();
        String subject = "스터디올레 '" + study.getTitle() + "' 스터디가 생겼습니다";
        String message = templateEngine.process("mail/simple-link", context);
        EmailMessage emailMessage = new EmailMessage(to, subject, message);
        emailService.sendEmail(emailMessage);
    }
}
