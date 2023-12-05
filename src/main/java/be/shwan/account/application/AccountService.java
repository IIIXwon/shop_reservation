package be.shwan.account.application;

import be.shwan.account.domain.Account;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.settings.dto.NicknameForm;
import be.shwan.settings.dto.Notifications;
import be.shwan.settings.dto.PasswordForm;
import be.shwan.settings.dto.ProfileInfo;
import be.shwan.tag.domain.Tag;
import be.shwan.zone.domain.Zone;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

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

    void sendEmailLoginUrl(Account account);

    void sendEmailLogin(Account account, String token);

    void addTag(Account account, Tag tag);

    Set<Tag> getTags(Account account);

    void removeTag(Account account, Tag tag);

    void addZone(Account account, Zone zone);

    void removeZone(Account account, Zone zone);

    Set<Zone> getZones(Account account);
}
