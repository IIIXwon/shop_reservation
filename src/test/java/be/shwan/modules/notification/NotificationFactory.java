package be.shwan.modules.notification;

import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFactory {
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    public static final String DEFAULT_TITLE = "title";
    public static final String DEFAULT_LINK = "link";
    public static final String DEFAULT_MESSAGE = "message";



    public Notification createNotification(String nickname, String notificationTitle, String notificationLink,
                                   String notificationMessage,NotificationType notificationType) {
        Account account = getByNickname(nickname);
        return save(notificationTitle, notificationLink, notificationMessage, notificationType, account);
    }

    public Notification createDefaultStudyCreatedNotification() {
        Account account = getByNickname(AccountFactory.DEFAULT_ACCOUNT_NAME);
        return save(DEFAULT_TITLE, DEFAULT_LINK, DEFAULT_MESSAGE, NotificationType.STUDY_CREATED, account);
    }

    public Notification createDefaultEventEnrollmentNotification() {
        Account account = getByNickname(AccountFactory.DEFAULT_ACCOUNT_NAME);
        return save(DEFAULT_TITLE, DEFAULT_LINK, DEFAULT_MESSAGE, NotificationType.EVENT_ENROLLMENT, account);
    }

    public Notification createDefaultStudyUpdatedNotification() {
        Account account = getByNickname(AccountFactory.DEFAULT_ACCOUNT_NAME);
        return save(DEFAULT_TITLE, DEFAULT_LINK, DEFAULT_MESSAGE, NotificationType.STUDY_UPDATED, account);
    }

    private Account getByNickname(String nickname) {
        return accountRepository.findByNickname(nickname);
    }

    private Notification save(String notificationTitle, String notificationLink, String notificationMessage, NotificationType notificationType, Account account) {
        return notificationRepository.save(new Notification(notificationTitle, notificationLink, notificationMessage,
                account, notificationType));
    }
}
