package be.shwan.account.presentation;

import be.shwan.account.domain.AccountRepository;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("[GET] /sign-up 회원가입 페이지 접근")
    @Test
    void testSignUpPage() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(model().attributeExists("signUpFormDto"))
                .andExpect(view().name(accountView))
                .andExpect(status().isOk());

    }

    @DisplayName("[POST] /sign-up 회원가입 성공")
    @Test
    void testSignUpSubmit() throws Exception {
        mockMvc.perform(post("/sign-up").with(csrf())
                        .param("nickname", "asdf")
                        .param("email", "asdf@adsf.com")
                        .param("password", "1234qwe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;

        assertTrue(accountRepository.existsByEmail("asdf@adsf.com"));

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
        ;
    }
}