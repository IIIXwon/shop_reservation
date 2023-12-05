package be.shwan.settings;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.settings.dto.Notifications;
import be.shwan.settings.dto.ProfileInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void init() throws Exception {
        String nickname = "seunghwan";
        String password = "12345678";
        SignUpFormDto request = SignUpFormDto.builder()
                .nickname(nickname)
                .password(password)
                .email("seunghw@dkjhds.com")
                .build();

        accountService.processNewAccount(request);
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

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[GET] /settings/password, 비밀번호 변경 페이지")
    @Test
    void testPasswordPage() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(view().name("/settings/password"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[POST] /settings/password, 비밀번호 변경 성공")
    @Test
    void testUpdatePassword() throws Exception {

        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "11223344")
                        .param("newPasswordConfirm", "11223344")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[POST] /settings/password, 비밀번호 변경 실패")
    @Test
    void testUpdatePassword_fail() throws Exception {

        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "11223344")
                        .param("newPasswordConfirm", "11223345")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[GET] /settings/notifications, 알림 변경 페이지")
    @Test
    void testNotificationPage() throws Exception {
        mockMvc.perform(get("/settings/notifications"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notifications"))
                .andExpect(view().name("/settings/notifications"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithUserDetails(value = "seunghwan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[POST] /settings/notifications, 알림 변경 성공")
    @Test
    void testUpdateNotification() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                        .param("studyCreatedByEmail", Boolean.TRUE.toString())
                        .param("studyCreatedByWeb", Boolean.TRUE.toString())
                        .param("studyEnrollmentResultByEmail", Boolean.TRUE.toString())
                        .param("studyEnrollmentResultByWeb", Boolean.TRUE.toString())
                        .param("studyUpdatedByEmail", Boolean.TRUE.toString())
                        .param("studyUpdatedByWeb", Boolean.TRUE.toString())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notifications"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;

    }

}