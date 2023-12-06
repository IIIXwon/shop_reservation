package be.shwan.mail.application.impl;

import be.shwan.mail.application.EmailService;
import be.shwan.mail.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class HtmlEmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.toString());
            mimeMessageHelper.setTo(emailMessage.to());
            mimeMessageHelper.setSubject(emailMessage.subject());
            mimeMessageHelper.setText(emailMessage.message(), true);
            javaMailSender.send(mimeMessage);
            log.debug("console {}", "/check-email-token?token=" + emailMessage.message());
        } catch (MessagingException e) {
            log.error("failed to send email", e);
        }
    }
}
