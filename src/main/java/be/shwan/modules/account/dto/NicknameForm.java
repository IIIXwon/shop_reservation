package be.shwan.modules.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record NicknameForm(
        @NotBlank
        @Length(min = 1, max = 10)
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{1,10}$")
        String nickname) {
}
