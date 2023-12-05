package be.shwan.account.application;

import be.shwan.account.domain.Account;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.settings.dto.NicknameForm;
import be.shwan.settings.dto.Notifications;
import be.shwan.settings.dto.PasswordForm;
import be.shwan.settings.dto.ProfileInfo;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService{
    AccountResponseRecord getAccountInfo(Long id);

    Account processNewAccount(SignUpFormDto signUpFormDto) throws Exception;

    void login(Account account);

    void sendEmailToken(Account account);

    void completeSignUp(Account account);

    void updateProfile(Account account, ProfileInfo profileInfo);

    void updatePassword(Account account, PasswordForm passwordForm);

    void updateNotification(Account account, Notifications notifications);

    void updateAccount(Account account, NicknameForm signUpFormDto);
}
