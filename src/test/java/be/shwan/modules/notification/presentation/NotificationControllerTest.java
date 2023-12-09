package be.shwan.modules.notification.presentation;

import be.shwan.infra.AbstractContainerBaseTest;
import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.WithAccount;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.notification.NotificationFactory;
import be.shwan.modules.notification.application.NotificationService;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class NotificationControllerTest extends AbstractContainerBaseTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationFactory notificationFactory;

    @Autowired
    AccountFactory accountFactory;

    @WithAccount(AccountFactory.DEFAULT_ACCOUNT_NAME)
    @DisplayName("[GET] /notifications, 알림 페이지, 알림 미확인")
    @Test
    void notificationView() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name(NotificationController.NOTIFICATION_VIEW))
                .andExpect(model().attributeExists("isNew", "numberOfNotChecked", "numberOfChecked", "account"
                        , "notifications", "newStudyNotifications", "eventEnrollmentNotifications", "watchingStudyNotifications"
                ))
                .andExpect(authenticated().withUsername(AccountFactory.DEFAULT_ACCOUNT_NAME))
        ;
    }

    @WithAccount(AccountFactory.DEFAULT_ACCOUNT_NAME)
    @DisplayName("[GET] /notifications/old, 알림 페이지, 알림 확인")
    @Test
    void notificationOldView() throws Exception {
        mockMvc.perform(get("/notifications/old"))
                .andExpect(status().isOk())
                .andExpect(view().name(NotificationController.NOTIFICATION_VIEW))
                .andExpect(model().attributeExists("isNew", "numberOfNotChecked", "numberOfChecked", "account"
                        , "notifications", "newStudyNotifications", "eventEnrollmentNotifications", "watchingStudyNotifications"
                ))
                .andExpect(authenticated().withUsername(AccountFactory.DEFAULT_ACCOUNT_NAME))
        ;
    }

    @WithAccount(AccountFactory.DEFAULT_ACCOUNT_NAME)
    @DisplayName("[DELETE] /notifications, 읽은 알람 삭제")
    @Test
    void deleteNotification() throws Exception {
        notificationFactory.createDefaultStudyCreatedNotification();
        Account account = accountFactory.findAccountByNickname(AccountFactory.DEFAULT_ACCOUNT_NAME);
        List<Notification> notifications = notificationService.getNotificationByAccount(account);
        notificationService.readNotification(notifications);
        mockMvc.perform(delete("/notifications")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"))
                .andExpect(authenticated().withUsername(AccountFactory.DEFAULT_ACCOUNT_NAME))
        ;
        assertEquals(0, notificationRepository.countByAccountAndChecked(account, true));
    }
}