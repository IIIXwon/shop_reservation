package be.shwan.modules.account.event;

import be.shwan.infra.mail.dto.EmailMessage;
import org.springframework.context.ApplicationEvent;

public record EmailCreateEvent(EmailMessage emailMessage) {
}
