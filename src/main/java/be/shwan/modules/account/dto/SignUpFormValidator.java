package be.shwan.modules.account.dto;

import be.shwan.modules.account.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpFormDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpFormDto signUpFormDto = (SignUpFormDto) target;
        if (accountRepository.existsByEmail(signUpFormDto.email())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpFormDto.email()}, "이미 사용 중인 이메일입니다");
        }

        if (accountRepository.existsByNickname(signUpFormDto.nickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpFormDto.nickname()}, "이미 사용 중인 닉네임 입니다");
        }
    }
}
