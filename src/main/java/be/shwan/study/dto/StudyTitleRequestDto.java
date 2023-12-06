package be.shwan.study.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record StudyTitleRequestDto(
        @NotBlank
        @Length(max = 50)
        String newTitle
) {
}
