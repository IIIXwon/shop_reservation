package be.shwan.modules.account;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.dto.SignUpFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    public static final String DEFAULT_ACCOUNT_NAME = "testUser";
    public static final String DEFAULT_ACCOUNT_PASSWORD = "12345678";

    public static final String DEFAULT_ACCOUNT_EMAIL = DEFAULT_ACCOUNT_NAME + "@email.com";
    public Account createAccount(String nickname) throws Exception {
        SignUpFormDto signUpForm = getDefaultSignUpFormDto(nickname);
        return accountService.processNewAccount(signUpForm);
    }

    public Account findAccountByNickname(String nickname) throws Exception {
        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            return createAccount(nickname);
        }
        return account;
    }

    private SignUpFormDto getDefaultSignUpFormDto(String nickname) {
        return new SignUpFormDto(nickname, nickname + "@email.com", "12345678");
    }

    public Account createDefaultAccount() throws Exception {
        return createAccount(DEFAULT_ACCOUNT_NAME);
    }
}
