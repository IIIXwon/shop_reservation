package be.shwan.mail.application;

import be.shwan.mail.dto.EmailMessage;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
