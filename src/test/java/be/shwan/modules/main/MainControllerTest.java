package be.shwan.modules.main;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.dto.SignUpFormDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void init() throws Exception {
        String nickname = "seunghwan";
        String password = "12345678";
        SignUpFormDto request = SignUpFormDto.builder()
                .nickname(nickname)
                .password(password)
                .email("seunghw@dkjhds.com")
                .build();

        Account account = accountService.processNewAccount(request);
    }

    @AfterEach()
    void endEach() {
        accountRepository.deleteAll();
    }
    @DisplayName("[POST] /login, nickname login 성공")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "seunghwan")
                        .param("password", "12345678")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @DisplayName("[POST] /login, email login 성공")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "seunghw@dkjhds.com")
                        .param("password", "12345678")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @DisplayName("[POST] /login, login 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "seung22w@dkjhds.com")
                        .param("password", "12345678")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated())
        ;
    }

    @WithMockUser
    @DisplayName("[POST] /logout, logout 성공")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated())
        ;
    }
}