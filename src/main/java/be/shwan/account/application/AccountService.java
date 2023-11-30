package be.shwan.account.application;

import be.shwan.account.domain.Account;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account signUp(SignUpRequestDto requestDto) throws Exception;

    AccountResponseRecord getAccountInfo(Long id);
}
