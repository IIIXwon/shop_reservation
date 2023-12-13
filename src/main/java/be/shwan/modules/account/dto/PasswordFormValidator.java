package be.shwan.modules.account.dto;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.newPassword().equals(passwordForm.newPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.newPassword", "입력한 비밀번호가 일치하지 않습니다.");
            errors.rejectValue("newPasswordConfirm", "wrong.newPasswordConfirm", "입력한 비밀번호가 일치하지 않습니다.");
        }
    }
}
