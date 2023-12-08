package be.shwan.modules.account.presentation;

import be.shwan.infra.AbstractContainerBaseTest;
import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.WithAccount;
import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class SettingsControllerTest extends AbstractContainerBaseTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;
    
    @Autowired
    AccountFactory accountFactory;

    @Autowired
    TagService tagService;

    @Autowired
    ZoneService zoneService;

    @Autowired
    ObjectMapper objectMapper;

    private final String USER_NAME = "seunghwan";

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /settings/profile, 프로필 작성 페이지 화면")
    @Test
    void testProfilePage() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.PROFILE_VIEW))
                .andExpect(model().attributeExists("account", "profileInfo"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
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
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
        
        Account byNickname = accountFactory.findAccountByNickname(USER_NAME);
        assertEquals(bio, byNickname.getBio());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/profile, 프로필 정보 업데이트 실패")
    @Test
    void testUpdateProfile_fail() throws Exception {
        String bio = "biobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobiobio";

        mockMvc.perform(post("/settings/profile").with(csrf())
                        .param("bio", bio)
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.PROFILE_VIEW))
                .andExpect(model().attributeExists("account", "profileInfo"))
                .andExpect(model().hasErrors())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /settings/password, 비밀번호 변경 페이지")
    @Test
    void testPasswordPage() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(view().name(SettingsController.PASSWORD_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
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
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/password, 비밀번호 변경 실패")
    @Test
    void testUpdatePassword_fail() throws Exception {

        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "11223344")
                        .param("newPasswordConfirm", "11223345")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.PASSWORD_VIEW))
                .andExpect(model().hasErrors())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /settings/notifications, 알림 변경 페이지")
    @Test
    void testNotificationPage() throws Exception {
        mockMvc.perform(get("/settings/notifications"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notifications"))
                .andExpect(view().name(SettingsController.NOTIFICATION_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
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
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
        Account byNickname = accountFactory.findAccountByNickname(USER_NAME);
        assertTrue(byNickname.isStudyUpdatedByWeb());
        assertTrue(byNickname.isStudyUpdatedByEmail());
    }


    @WithAccount(USER_NAME)
    @DisplayName("[GET] /settings/account, 계정 정보 변경 페이지")
    @Test
    void testAccountPage() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(view().name(SettingsController.ACCOUNT_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/account, 계정 변경 성공")
    @Test
    void testUpdateAccount() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("nickname", "2hwan")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername("2hwan"))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/account, 계정 변경 실패")
    @Test
    void testUpdateAccount_fail() throws Exception {

        mockMvc.perform(post("/settings/account")
                        .param("nickname", "*****sdlfj")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.ACCOUNT_VIEW))
                .andExpect(model().hasErrors())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /settings/tags, 관심 주제 페이지")
    @Test
    void testTagPage() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.TAG_VIEW))
                .andExpect(model().attributeExists("tags", "whitelist"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/tag/add, 관심 주제 추가")
    @Test
    void testUpdateTag() throws Exception {
        RequestTagDto requestTagDto = new RequestTagDto("리그오브레전드");
        mockMvc.perform(post("/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Tag tag = tagService.getTag(new RequestTagDto("리그오브레전드"));
        assertNotNull(tag);
        Account byNickname = accountFactory.findAccountByNickname(USER_NAME);
        assertTrue(byNickname.getTags().contains(tag));
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/tag/remove, 관심 주제 삭제")
    @Test
    void testRemoveTag() throws Exception {
        RequestTagDto requestTagDto = new RequestTagDto("리그오브레전드");
        Tag tag = tagService.getTag(requestTagDto);

        Account byNickname = accountFactory.findAccountByNickname(USER_NAME);
        accountService.addTag(byNickname, tag);

        mockMvc.perform(post("/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
        assertEquals(0, byNickname.getTags().size());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /settings/zones, 활동 지역 페이지")
    @Test
    void testZonePage() throws Exception {
        mockMvc.perform(get("/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.ZONE_VIEW))
                .andExpect(model().attributeExists("zones", "whitelist"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/zones/add, 활동 지역 추가")
    @Test
    void testUpdateZone() throws Exception {
        RequestZoneDto requestZoneDto = new RequestZoneDto("Gunsan(군산시)/North Jeolla");
        mockMvc.perform(post("/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Zone zone = zoneService.findZone("Gunsan(군산시)/North Jeolla");
        Account byNickname = accountFactory.findAccountByNickname(USER_NAME);
        assertTrue(byNickname.getZones().contains(zone));
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /settings/zones/remove, 활동 지역 삭제")
    @Test
    void testRemoveZone() throws Exception {
        Account byNickname = accountFactory.findAccountByNickname(USER_NAME);
        RequestZoneDto requestZoneDto = new RequestZoneDto("Gunsan(군산시)/North Jeolla");
        Zone zone = zoneService.findZone("Gunsan(군산시)/North Jeolla");
        accountService.addZone(byNickname, zone);
        mockMvc.perform(post("/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        assertEquals(0, byNickname.getZones().size());
    }
}