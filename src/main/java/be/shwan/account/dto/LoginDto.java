package be.shwan.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

public record LoginDto(
        @NotBlank
        @Length(min = 1, max = 10)
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{1,10}$")
        String usernameOrEmail,
        @NotBlank
        @Length(min = 6, max = 50)
        String password) {

    @Builder
    public LoginDto(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
}
