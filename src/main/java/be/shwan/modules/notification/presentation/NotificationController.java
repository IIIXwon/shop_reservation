package be.shwan.modules.notification.presentation;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.notification.application.NotificationService;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    static final String NOTIFICATION_VIEW = "notification/list";

    @GetMapping(value = {"/notifications"})
    public String notificationPage(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationService.getNotificationByAccount(account);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        long numberOfChecked = notifications.size() - numberOfNotChecked;
        putCategorizedNotifications(account, model, notifications, numberOfNotChecked, numberOfChecked);
        model.addAttribute("isNew", true);
        notificationService.readNotification(notifications);
        return NOTIFICATION_VIEW;
    }



    @GetMapping(value = { "/notifications/old"})
    public String notificationOldPage(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationService.getNotificationByAccount(account);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        long numberOfNotChecked = notifications.size() - numberOfChecked;
        putCategorizedNotifications(account, model, notifications, numberOfNotChecked, numberOfChecked);
        model.addAttribute("isNew", false);
        return NOTIFICATION_VIEW;
    }

    @DeleteMapping(value = "/notifications")
    public String deleteNotification(@CurrentUser Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Account account, Model model, List<Notification> notifications,
                                             long numberOfNotChecked, long numberOfChecked) {
        model.addAttribute(account);
        model.addAttribute("notifications", notifications);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("newStudyNotifications", notificationService.selectNewStudyNotification(notifications));
        model.addAttribute("watchingStudyNotifications", notificationService.selectWatchingStudyNotification(notifications));
        model.addAttribute("eventEnrollmentNotifications", notificationService.selectEventEnrollmentNotification(notifications));
    }
}
