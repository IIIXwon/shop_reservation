package be.shwan.modules.main;

import be.shwan.infra.AbstractContainerBaseTest;
import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.WithAccount;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class MainControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyFactory studyFactory;

    @DisplayName("[GET] /, 비 로그인 메인페이지 접근")
    @Test
    void mainPage() throws Exception {
        Account account = accountFactory.findAccountByNickname(AccountFactory.DEFAULT_ACCOUNT_NAME);
        studyService.generateTestdatas(account);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studyList"))
                .andExpect(view().name(MainController.MAIN_VIEW))
                .andExpect(unauthenticated())
        ;

        List<Study> studyList = studyRepository.findDefault();
        assertEquals(9, studyList.size());
    }

    @WithAccount(AccountFactory.DEFAULT_ACCOUNT_NAME)
    @DisplayName("[GET] /, 로그인 메인페이지 접근")
    @Test
    void mainPageWithLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "enrollmentList", "studyManagerOf", "studyMemberOf", "studyList"))
                .andExpect(view().name(MainController.LOGIN_MAIN_VIEW))
                .andExpect(authenticated().withUsername(AccountFactory.DEFAULT_ACCOUNT_NAME))
        ;
    }

    @DisplayName("[POST] /login, nickname login 성공")
    @Test
    void login_with_nickname() throws Exception {
        String nickname = "testUser";
        accountFactory.createAccount(nickname);

        mockMvc.perform(post("/login").with(csrf())
                        .param("username", nickname)
                        .param("password", AccountFactory.DEFAULT_ACCOUNT_PASSWORD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(nickname))
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