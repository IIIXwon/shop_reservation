package be.shwan.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

public record SignUpRequestDto(
        @NotBlank
        @Length(min = 1, max = 10)
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{1,10}$")
        String nickname,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Length(min = 6, max = 50)
        String password
) {

    @Builder
    public SignUpRequestDto(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}
