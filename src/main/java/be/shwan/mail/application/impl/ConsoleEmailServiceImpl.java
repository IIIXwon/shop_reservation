package be.shwan.mail.application.impl;

import be.shwan.mail.application.EmailService;
import be.shwan.mail.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile({"local", "test"})
@Service
public class ConsoleEmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("send email: {}", emailMessage.message());
    }
}
