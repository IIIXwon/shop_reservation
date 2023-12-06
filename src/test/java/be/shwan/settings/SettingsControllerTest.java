package be.shwan.settings;

import be.shwan.WithAccount;
import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.tag.domain.Tag;
import be.shwan.tag.domain.TagRepository;
import be.shwan.tag.dto.RequestTagDto;
import be.shwan.zone.application.ZoneService;
import be.shwan.zone.domain.Zone;
import be.shwan.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ZoneService zoneService;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void clear() {
        accountRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
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

        Account byNickname = accountRepository.findByNickname("seunghwan");
        assertEquals(bio, byNickname.getBio());
    }

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
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
        Account byNickname = accountRepository.findByNickname("seunghwan");
        assertTrue(byNickname.isStudyUpdatedByWeb());
        assertTrue(byNickname.isStudyUpdatedByEmail());
    }


    @WithAccount("seunghwan")
    @DisplayName("[GET] /settings/account, 계정 정보 변경 페이지")
    @Test
    void testAccountPage() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(view().name("/settings/account"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithAccount("seunghwan")
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

    @WithAccount("seunghwan")
    @DisplayName("[POST] /settings/account, 계정 변경 실패")
    @Test
    void testUpdateAccount_fail() throws Exception {

        mockMvc.perform(post("/settings/account")
                        .param("nickname", "*****sdlfj")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/settings/account"))
                .andExpect(model().hasErrors())
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithAccount("seunghwan")
    @DisplayName("[GET] /settings/tags, 관심 주제 페이지")
    @Test
    void testTagPage() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("/settings/tags"))
                .andExpect(model().attributeExists("tags", "whitelist"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithAccount("seunghwan")
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
                .andExpect(authenticated().withUsername("seunghwan"))
        ;

        Tag tag = tagRepository.findByTitle("리그오브레전드");
        assertNotNull(tag);
        Account byNickname = accountRepository.findByNickname("seunghwan");
        assertTrue(byNickname.getTags().contains(tag));
    }

    @WithAccount("seunghwan")
    @DisplayName("[POST] /settings/tag/remove, 관심 주제 삭제")
    @Test
    void testRemoveTag() throws Exception {
        Account byNickname = accountRepository.findByNickname("seunghwan");
        RequestTagDto requestTagDto = new RequestTagDto("리그오브레전드");
        Tag tag = new Tag(requestTagDto);
        tagRepository.save(tag);
        accountService.addTag(byNickname, tag);
        mockMvc.perform(post("/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
        assertEquals(0, byNickname.getTags().size());
    }

    @WithAccount("seunghwan")
    @DisplayName("[GET] /settings/zones, 활동 지역 페이지")
    @Test
    void testZonePage() throws Exception {
        mockMvc.perform(get("/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("/settings/zones"))
                .andExpect(model().attributeExists("zones", "whitelist"))
                .andExpect(authenticated().withUsername("seunghwan"))
        ;
    }

    @WithAccount("seunghwan")
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
                .andExpect(authenticated().withUsername("seunghwan"))
        ;

        Zone zone = zoneService.findZone("Gunsan(군산시)/North Jeolla");
        Account byNickname = accountRepository.findByNickname("seunghwan");
        assertTrue(byNickname.getZones().contains(zone));
    }

    @WithAccount("seunghwan")
    @DisplayName("[POST] /settings/zones/remove, 활동 지역 삭제")
    @Test
    void testRemoveZone() throws Exception {
        Account byNickname = accountRepository.findByNickname("seunghwan");
        RequestZoneDto requestZoneDto = new RequestZoneDto("Gunsan(군산시)/North Jeolla");
        Zone zone = zoneService.findZone("Gunsan(군산시)/North Jeolla");
        accountService.addZone(byNickname, zone);
        mockMvc.perform(post("/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("seunghwan"))
        ;

        assertEquals(0, byNickname.getZones().size());
    }
}