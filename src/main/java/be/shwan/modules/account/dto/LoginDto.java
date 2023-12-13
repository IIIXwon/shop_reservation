package be.shwan.modules.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

public record LoginDto(
        @NotBlank
        @Length(min = 1, max = 100)
        String usernameOrEmail,
        @NotBlank
        @Length(min = 6, max = 50)
        String password) {
}
