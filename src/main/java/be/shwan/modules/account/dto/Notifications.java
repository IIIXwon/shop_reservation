package be.shwan.modules.account.dto;

public record Notifications(
        boolean studyCreatedByEmail,
        boolean studyCreatedByWeb,
        boolean studyEnrollmentResultByEmail,
        boolean studyEnrollmentResultByWeb,
        boolean studyUpdatedByEmail,
        boolean studyUpdatedByWeb
    ) {
}
