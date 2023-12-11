package be.shwan.modules.study.event;

import be.shwan.infra.config.AppProperties;
import be.shwan.infra.mail.application.EmailService;
import be.shwan.infra.mail.dto.EmailMessage;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountPredicates;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.notification.domain.NotificationType;
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

import java.util.HashSet;
import java.util.Set;

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
    public void studyEventHandlerWithCreated(StudyCreatedEvent studyEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyEvent.study().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendStudyEmail(account, study,
                        "스터디가 변경되었습니다.", "스터디올레 '" + study.getTitle() + "' 스터디가 생겼습니다");
            }

            if(account.isStudyCreatedByWeb()) {
                createdNotification(account, study, study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void studyEventHandlerWithUpdated(StudyUpdatedEvent studyEvent) {
        Study study = studyRepository.findStudyWithMembersAndManagersByPath(studyEvent.study().getPath());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());
        accounts.forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                sendStudyEmail(account, study,
                        studyEvent.message(),  "스터디올레 '" + study.getTitle() + "' 스터디에 새소식이 있습니다.");
            }

            if (account.isStudyUpdatedByWeb()) {
                createdNotification(account, study, studyEvent.message(), NotificationType.STUDY_UPDATED);
            }
        });
    }

    private void createdNotification(Account account, Study study, String message, NotificationType notificationType) {
        String title = study.getTitle();
        String link = "/study/" + study.getEncodePath();
        Notification notification = new Notification(title, link, message, account, notificationType);
        notificationRepository.save(notification);
    }


    private void sendStudyEmail(Account account, Study study, String contextMessage, String subject) {
        String message = setEmailMessage(account, study, contextMessage);
        sendEmail(account.getEmail(), subject, message);
    }

    private String setEmailMessage(Account account, Study study, String contextMessage) {
        Context context = setContext(account, study, contextMessage);
        return templateEngine.process("mail/simple-link", context);
    }

    private Context setContext(Account account, Study study, String contextMessage) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", contextMessage);
        context.setVariable("linkName", study.getTitle());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", "/study/" + study.getEncodePath());
        return context;
    }

    private void sendEmail(String to, String subject, String message) {
        EmailMessage emailMessage = new EmailMessage(to, subject, message);
        emailService.sendEmail(emailMessage);
    }
}
