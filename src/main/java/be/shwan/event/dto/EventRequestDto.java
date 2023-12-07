package be.shwan.event.dto;

import be.shwan.event.domain.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EventRequestDto(
        @NotBlank
        String title,
        EventType eventType,
        @Min(2)
        int limitOfEnrollments,
        @NotBlank
        String endEnrollmentDateTime,
        @NotBlank
        String startDateTime,
        @NotBlank
        String endDateTime,
        String description) {
}
