package be.shwan.infra.mail.dto;

public record EmailMessage(String to, String subject, String message) {
}
