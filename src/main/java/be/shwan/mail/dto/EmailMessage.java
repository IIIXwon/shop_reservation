package be.shwan.mail.dto;

public record EmailMessage(String to, String subject, String message) {
}
