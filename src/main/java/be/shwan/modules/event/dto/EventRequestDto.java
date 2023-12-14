package be.shwan.modules.event.dto;

import be.shwan.modules.event.domain.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

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
