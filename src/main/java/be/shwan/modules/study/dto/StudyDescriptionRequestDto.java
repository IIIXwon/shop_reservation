package be.shwan.modules.study.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record StudyDescriptionRequestDto(
        @NotBlank
        @Length(max = 100)
        String shortDescription,
        @NotBlank
        String fullDescription
        )
        {}
