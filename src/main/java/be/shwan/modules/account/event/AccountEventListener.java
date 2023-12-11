package be.shwan.modules.account.event;

import be.shwan.infra.mail.application.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventListener {
    private final EmailService emailService;

    @EventListener
    public void accountMailHandler(EmailCreateEvent createEvent) {
        emailService.sendEmail(createEvent.emailMessage());
    }
}
