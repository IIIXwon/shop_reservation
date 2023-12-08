package be.shwan.modules.account.presentation;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.mail.application.EmailService;
import be.shwan.infra.mail.dto.EmailMessage;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;
    @MockBean
    EmailService emailService;

    @DisplayName("[GET] /sign-up 회원가입 페이지 접근")
    @Test
    void testSignUpPage() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(model().attributeExists("signUpFormDto"))
                .andExpect(view().name(AccountController.SIGN_UP_PAGE))
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

        Account account =  accountFactory.findAccountByNickname(nickname);
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("[POST] /sign-up 회원가입 실패")
    @Test
    void testSignUpSubmit_fail() throws Exception {
        mockMvc.perform(post("/sign-up").with(csrf())
                        .param("nickname", "asdf128391273"))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.SIGN_UP_PAGE))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors())
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("[GET] /check-email-token 이메일 확인")
    @Test
    @Transactional
    void testCheckEmailToken() throws Exception {
        String nickname = "asdf";
        Account account = accountFactory.createAccount(nickname);
        account.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nickname", "numberOfUser"))
                .andExpect(model().attribute("nickname", nickname))
                .andExpect(view().name(AccountController.CHECKED_EMAIL_VIEW))
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
                .andExpect(view().name(AccountController.CHECKED_EMAIL_VIEW))
                .andExpect(unauthenticated())

        ;
    }

    @DisplayName("[GET] /email-login, 이메일 로그인 화면")
    @Test
    void testEmailLoginPage() throws Exception {
        mockMvc.perform(get("/email-login"))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.EMAIL_LOGIN_VIEW))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("[POST] /email-login, 이메일 로그인 url 발급 요청")
    @Test
    void testEmailLoginIssue() throws Exception {
        String nickname = "testUser";
        Account newAccount = accountFactory.createAccount(nickname);
        newAccount.issueEmailLoginToken();

        mockMvc.perform(post("/email-login").with(csrf())
                        .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.CHECK_LOGIN_EMAIL))
                .andExpect(unauthenticated())
        ;

        Account account = accountFactory.findAccountByNickname(nickname);
        assertNotNull(account);
        assertNotNull(account.getEmailLoginToken());
    }

    @DisplayName("[POST] /email-login, 이메일 로그인 url 발급 실패")
    @Test
    void testEmailLoginIssue_fail() throws Exception {
        mockMvc.perform(post("/email-login").with(csrf())
                        .param("email", "tes2t@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.CHECK_LOGIN_EMAIL))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("[POST] /login-by-email, 이메일 로그인 성공")
    @Test
    void testEmailLogin() throws Exception {

        String nickname = "testUser";
        Account account = accountFactory.createAccount(nickname);
        account.issueEmailLoginToken();

        mockMvc.perform(get("/login-by-email")
                        .param("email", account.getEmail())
                        .param("token", account.getEmailLoginToken()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.LOGGED_IN_BY_EMAIL))
                .andExpect(authenticated().withUsername(nickname))
        ;
    }

    @DisplayName("[POST] /login-by-email, 이메일 로그인 실패")
    @Test
    void testEmailLogin_fail() throws Exception {
        mockMvc.perform(get("/login-by-email")
                        .param("email", "")
                        .param("token", ""))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.LOGGED_IN_BY_EMAIL))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;
    }
}