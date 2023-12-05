package be.shwan.settings.dto;

import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.SignUpFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        if (accountRepository.existsByNickname(nicknameForm.nickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{nicknameForm.nickname()}, "이미 사용 중인 닉네임 입니다");
        }
    }
}
