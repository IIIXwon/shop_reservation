package be.shwan.account.application;

import be.shwan.account.domain.Account;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpFormDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService{
    AccountResponseRecord getAccountInfo(Long id);

    Account processNewAccount(SignUpFormDto signUpFormDto) throws Exception;

    void login(Account account);

    void sendEmailToken(Account account);
}
