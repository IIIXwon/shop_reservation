package be.shwan.modules.notification.application.impl;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.notification.application.NotificationService;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleNotificationImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void readNotification(List<Notification> notifications) {
        notifications.forEach(noti -> {
            if(!noti.isChecked())
                noti.check();
        });

    }



    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationByAccount(Account account) {
        return notificationRepository.findByAccountOrderByCreatedDateTime(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> selectNewStudyNotification(List<Notification> notifications) {
        return getNotificationsByType(notifications, NotificationType.STUDY_CREATED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> selectEventEnrollmentNotification(List<Notification> notifications) {
        return getNotificationsByType(notifications, NotificationType.EVENT_ENROLLMENT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> selectWatchingStudyNotification(List<Notification> notifications) {
        return getNotificationsByType(notifications, NotificationType.STUDY_UPDATED);
    }

    private List<Notification> getNotificationsByType(List<Notification> notifications, NotificationType notificationType) {
        return notifications.stream().filter(noti -> noti.getNotificationType().equals(notificationType)).toList();
    }

    private List<Notification> getNotCheckedNotification(List<Notification> notifications) {
        return notifications.stream().filter(noti -> !noti.isChecked()).toList();
    }
}
