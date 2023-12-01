package be.shwan.account.application;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.SignUpFormDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class AccountServiceTest {
   @Autowired private AccountService accountService;
   @Autowired private AccountRepository accountRepository;

    @Test
    void signUpTest() throws Exception {
        SignUpFormDto dto = SignUpFormDto.builder()
                .nickname("test")
                .password("password")
                .email("test@test.com")
                .build();

        Account account = accountService.processNewAccount(dto);
        assertEquals(1L, account.getId());
    }
    

}