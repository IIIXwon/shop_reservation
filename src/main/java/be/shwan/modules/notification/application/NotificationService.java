package be.shwan.modules.notification.application;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.notification.domain.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationByAccount(Account account);

    List<Notification> selectNewStudyNotification(List<Notification> notifications);

    List<Notification> selectEventEnrollmentNotification(List<Notification> notifications);

    List<Notification> selectWatchingStudyNotification(List<Notification> notifications);

    void readNotification(List<Notification> notifications);
}
