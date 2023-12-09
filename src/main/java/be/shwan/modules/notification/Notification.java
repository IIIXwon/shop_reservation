package be.shwan.modules.notification;

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

    private LocalDateTime createLocalDateTime;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

}
