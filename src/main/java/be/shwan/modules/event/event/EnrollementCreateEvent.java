package be.shwan.modules.event.event;

import be.shwan.infra.mail.dto.EmailMessage;
import org.springframework.context.ApplicationEvent;

public record EnrollementCreateEvent(EmailMessage emailMessage) {
}
