package be.shwan.settings;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.settings.dto.ProfileInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;


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
//        accountService.login(account);
    }

    @AfterEach
    void clear() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[GET] /settings/profile, 프로필 작성 페이지 화면")
    @Test
    void testProfilePage() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("/settings/profile"))
                .andExpect(model().attributeExists("account", "profileInfo"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[POST] /settings/profile, 프로필 정보 업데이트")
    @Test
    void testUpdateProfile() throws Exception {
        String url = "url";
        String bio = "bio";
        String occupation = "occupation";
        String location = "location";

        mockMvc.perform(post("/settings/profile").with(csrf())
                        .param(bio, bio)
                        .param(url, url)
                        .param(occupation, occupation)
                        .param(location, location)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[POST] /settings/profile, 프로필 정보 업데이트 실패")
    @Test
    void testUpdateProfile_fail() throws Exception {
        String bio = "biobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobio";

        mockMvc.perform(post("/settings/profile").with(csrf())
                        .param("bio", bio)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/settings/profile"))
                .andExpect(model().attributeExists("account", "profileInfo"))
                .andExpect(model().hasErrors())
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }
}