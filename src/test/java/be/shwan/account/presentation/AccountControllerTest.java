package be.shwan.account.presentation;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.SignUpFormDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    private final String accountView = "accounts/sign-up";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;
    @MockBean
    JavaMailSender javaMailSender;

    @AfterEach
    void end() {
        accountRepository.deleteAll();
    }
    @DisplayName("[GET] /sign-up 회원가입 페이지 접근")
    @Test
    void testSignUpPage() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(model().attributeExists("signUpFormDto"))
                .andExpect(view().name(accountView))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
        ;

    }

    @DisplayName("[POST] /sign-up 회원가입 성공")
    @Test
    void testSignUpSubmit() throws Exception {
        String nickname = "asdf";
        mockMvc.perform(post("/sign-up").with(csrf())
                        .param("nickname", nickname)
                        .param("email", "asdf@adsf.com")
                        .param("password", "1234qwe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername(nickname))
        ;

        Account account = accountRepository.findByEmail("asdf@adsf.com");
        assertTrue(accountRepository.existsByEmail("asdf@adsf.com"));
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("[POST] /sign-up 회원가입 실패")
    @Test
    void testSignUpSubmit_fail() throws Exception {
        mockMvc.perform(post("/sign-up").with(csrf())
                        .param("nickname", "asdf128391273"))
                .andExpect(status().isOk())
                .andExpect(view().name(accountView))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors())
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("[GET] /check-email-token 이메일 확인")
    @Test
    @Transactional
    void testCheckEmailToken() throws Exception {
        String email = "asdf@adsf.com";
        String nickname = "asdf";
        Account account = new Account(nickname, email, "1234qwe");
        accountRepository.save(account);
        account.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nickname", "numberOfUser"))
                .andExpect(model().attribute("nickname", nickname))
                .andExpect(view().name("accounts/checked-email"))
                .andExpect(authenticated().withUsername(nickname))
        ;
    }

    @DisplayName("[GET] /check-email-token 이메일 확인 실패")
    @Test
    void testCheckEmailToken_fail() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "dhshdlkhfglkhs")
                        .param("email", "sdfkhkshdfa@Sd.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("accounts/checked-email"))
                .andExpect(unauthenticated())

        ;
    }

    @DisplayName("[GET] /email-login, 이메일 로그인 화면")
    @Test
    void testEmailLoginPage() throws Exception {
        mockMvc.perform(get("/email-login"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/email-login"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("[POST] /email-login, 이메일 로그인 url 발급 요청")
    @Test
    void testEmailLoginIssue() throws Exception {
        SignUpFormDto testUser = SignUpFormDto.builder()
                .email("test@test.com")
                .password("12345678")
                .nickname("testUser")
                .build();
        accountService.processNewAccount(testUser);

        mockMvc.perform(post("/email-login").with(csrf())
                        .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/check-login-email"))
                .andExpect(unauthenticated())
        ;

        Account account = accountRepository.findByEmail("test@test.com");
        assertTrue(accountRepository.existsByEmail("test@test.com"));
        assertNotNull(account.getEmailLoginToken());
    }

    @DisplayName("[POST] /email-login, 이메일 로그인 url 발급 실패")
    @Test
    void testEmailLoginIssue_fail() throws Exception {
        mockMvc.perform(post("/email-login").with(csrf())
                        .param("email", "tes2t@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/check-login-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("[POST] /login-by-email, 이메일 로그인 성공")
    @Test
    void testEmailLogin() throws Exception {

        SignUpFormDto testUser = SignUpFormDto.builder()
                .email("test@test.com")
                .password("12345678")
                .nickname("testUser")
                .build();
        Account newAccount = accountService.processNewAccount(testUser);
        accountService.sendEmailLoginUrl(newAccount);

        mockMvc.perform(get("/login-by-email")
                        .param("email", newAccount.getEmail())
                        .param("token", newAccount.getEmailLoginToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/logged-in-by-email"))
                .andExpect(authenticated().withUsername("testUser"))
        ;
    }

    @DisplayName("[POST] /login-by-email, 이메일 로그인 실패")
    @Test
    void testEmailLogin_fail() throws Exception {
        mockMvc.perform(get("/login-by-email")
                        .param("email", "")
                        .param("token", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/logged-in-by-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;
    }
}