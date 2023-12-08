package be.shwan.infra.mail.application;

import be.shwan.infra.mail.dto.EmailMessage;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
