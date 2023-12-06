package be.shwan.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record StudyPathRequestDto(
        @NotBlank
        @Length(min = 2, max = 20)
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{2,20}$")
        String newPath
) {
}
