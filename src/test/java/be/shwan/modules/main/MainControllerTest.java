package be.shwan.modules.main;

import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @DisplayName("[POST] /login, nickname login 성공")
    @Test
    void login_with_nickname() throws Exception {
        accountFactory.createAccount(AccountFactory.DEFAULT_ACCOUNT_NAME);

        mockMvc.perform(post("/login").with(csrf())
                        .param("username", AccountFactory.DEFAULT_ACCOUNT_NAME)
                        .param("password", AccountFactory.DEFAULT_ACCOUNT_PASSWORD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(AccountFactory.DEFAULT_ACCOUNT_NAME))
        ;
    }

    @DisplayName("[POST] /login, email login 성공")
    @Test
    void login_with_email() throws Exception {
        accountFactory.createAccount(AccountFactory.DEFAULT_ACCOUNT_NAME);

        mockMvc.perform(post("/login").with(csrf())
                        .param("username", AccountFactory.DEFAULT_ACCOUNT_EMAIL)
                        .param("password", AccountFactory.DEFAULT_ACCOUNT_PASSWORD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(AccountFactory.DEFAULT_ACCOUNT_NAME))
        ;
    }

    @DisplayName("[POST] /login, login 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", AccountFactory.DEFAULT_ACCOUNT_EMAIL)
                        .param("password", "11223344")
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