package be.shwan.modules.notification.domain;

import be.shwan.modules.account.domain.Account;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDateTime createdDateTime;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    public Notification(String title, String link, String message, Account account, NotificationType notificationType) {
        this.title = title;
        this.link = link;
        this.message = message;
        this.account = account;
        createdDateTime = LocalDateTime.now();
        this.notificationType = notificationType;
    }

    public void check() {
        this.checked = true;
    }
}
