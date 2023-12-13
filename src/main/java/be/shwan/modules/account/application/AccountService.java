package be.shwan.modules.account.application;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.*;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.zone.domain.Zone;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface AccountService {
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

    String generateToken(LoginDto loginDto);
}
