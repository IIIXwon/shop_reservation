package be.shwan.settings.dto;

public record Notifications(
        boolean studyCreatedByEmail,
        boolean studyCreatedByWeb,
        boolean studyEnrollmentResultByEmail,
        boolean studyEnrollmentResultByWeb,
        boolean studyUpdatedByEmail,
        boolean studyUpdatedByWeb
    ) {
}
