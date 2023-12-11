package be.shwan.modules.event.event;

import be.shwan.infra.config.AppProperties;
import be.shwan.infra.mail.dto.EmailMessage;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.notification.domain.NotificationType;
import be.shwan.modules.study.domain.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
public class EnrollmentEventListener {
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    private final TemplateEngine templateEngine;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void enrollmentEventHandler(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.enrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();
        String accept = "스터디올레 '" + event.getTitle() + "' 모임에 참가 신청이 완료되었습니다.";
        String reject = "스터디올레 '" + event.getTitle() + "' 모임에 참가 신청이 거절되었습니다.";
        if (account.isStudyEnrollmentResultByEmail()) {
            sendStudyEmail(account, study, enrollmentEvent.message(), enrollment.isAccepted() ? accept : reject);
        }

        if (account.isStudyEnrollmentResultByWeb()) {
            createdNotification(account, study, enrollmentEvent.message(), NotificationType.EVENT_ENROLLMENT);
        }
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
        eventPublisher.publishEvent(new EnrollementCreateEvent(emailMessage));
    }
}
