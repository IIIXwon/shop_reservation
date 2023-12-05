package be.shwan.settings.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PasswordForm(
        @NotBlank
        @Length(min = 6, max = 50)
        String newPassword,
        @NotBlank
        @Length(min = 6, max = 50)
        String newPasswordConfirm) {
}
