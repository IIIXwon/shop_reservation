package be.shwan.modules.study.presentation;

import be.shwan.infra.AbstractContainerBaseTest;
import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.WithAccount;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudyControllerTest extends AbstractContainerBaseTest {
    private final String USER_NAME = "seunghwan";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyService studyService;

    @Autowired
    TagService tagService;

    @Autowired
    ObjectMapper objectMapper;

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /new-study, 스터디 개설 페이지")
    @Test
    void testStudyFormPage() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studyRequestDto"))
                .andExpect(view().name(StudyController.STUDY_FORM_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /new-study, 스터디 개설")
    @Test
    void testNewStudy() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        mockMvc.perform(post("/new-study")
                        .param("path", StudyFactory.DEFAULT_PATH)
                        .param("title", StudyFactory.DEFAULT_TITLE)
                        .param("shortDescription", StudyFactory.DEFAULT_SHORT_DESCRIPTION)
                        .param("fullDescription", StudyFactory.DEFAULT_FULL_DESCRIPTION)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
        assertNotNull(studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager));
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /new-study, 스터디 개설 실패: 같은 경로의 path가 존재함")
    @Test
    void testNewStudy_fail() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/new-study")
                        .param("path", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name(StudyController.STUDY_FORM_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}, 스터디 상세 페이지")
    @Test
    void testStudyViewPage() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);
        mockMvc.perform(get("/study/{path}", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "study"))
                .andExpect(view().name(StudyController.STUDY_VIEW_PATH))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/members, 스터디 참가자 페이지")
    @Test
    void testStudyMemberPage() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/members", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(StudyController.STUDY_MEMBER_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/description, 스터디 수정 페이지 description")
    @Test
    void testStudySettingsPageDescription() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/settings/description", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study", "studyDescriptionRequestDto"))
                .andExpect(view().name(StudySettingsController.STUDY_SETTING_DESCRIPTION_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/description, 스터디 수정 페이지 description")
    @Test
    void testUpdateStudyDescription() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        String shortDescription = "한글";
        String fullDescription = "한글로 바꿨습니다";

        mockMvc.perform(post("/study/{path}/settings/description", StudyFactory.DEFAULT_PATH)
                        .param("shortDescription", shortDescription)
                        .param("fullDescription", fullDescription)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/description"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study byPath = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);

        assertEquals(shortDescription, byPath.getShortDescription());
        assertEquals(fullDescription, byPath.getFullDescription());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/banner, 스터디 수정 페이지 banner")
    @Test
    void testStudySettingsPageBanner() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/settings/banner", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(StudySettingsController.STUDY_SETTING_BANNER_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/banner/enable, 스터디 수정 banner enable")
    @Test
    void testUpdateStudyEnableBanner() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/study/{path}/settings/banner/enable", StudyFactory.DEFAULT_PATH)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/banner"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study byPath = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertTrue(byPath.isUseBanner());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/banner/disable, 스터디 수정 banner disable")
    @Test
    void testUpdateStudyDisableBanner() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/study/{path}/settings/banner/disable", StudyFactory.DEFAULT_PATH)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/banner"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study byPath = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertFalse(byPath.isUseBanner());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/tags, 스터디 수정 페이지 tag")
    @Test
    void testStudySettingsPageTags() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/settings/tags", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("whitelist", "study", "tags"))
                .andExpect(view().name("study/settings/tags"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/tags/add, 스터디 tag 추가")
    @Test
    void testStudyTagsAdd() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);
        RequestTagDto requestTagDto = new RequestTagDto("game");

        mockMvc.perform(post("/study/{path}/settings/tags/add", StudyFactory.DEFAULT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertEquals(1, study.getTags().size());
    }


    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/tags/remove, 스터디 tag 제거")
    @Test
    void testStudyTagsRemove() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        RequestTagDto requestTagDto = new RequestTagDto("game");
        tagService.getTag(requestTagDto);

        mockMvc.perform(post("/study/{path}/settings/tags/remove", StudyFactory.DEFAULT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertEquals(0, study.getTags().size());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/zones, 스터디 수정 페이지 zone")
    @Test
    void testStudySettingsPageZones() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/settings/zones", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("whitelist", "study", "zones"))
                .andExpect(view().name(StudySettingsController.STUDY_SETTING_ZONE_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/zones/add, 스터디 zone 추가")
    @Test
    void testStudyZonesAdd() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        RequestZoneDto requestZoneDto = new RequestZoneDto("Andong(안동시)/North Gyeongsang");

        mockMvc.perform(post("/study/{path}/settings/zones/add", StudyFactory.DEFAULT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertEquals(1, study.getZones().size());
    }


    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/zones/remove, 스터디 zone 제거")
    @Test
    void testStudyZonesRemove() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);
        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);

        RequestZoneDto requestZoneDto = new RequestZoneDto("Andong(안동시)/North Gyeongsang");
        studyService.addZone(study, requestZoneDto);

        mockMvc.perform(post("/study/{path}/settings/zones/remove", StudyFactory.DEFAULT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        assertEquals(0, study.getZones().size());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/study, 스터디 수정 페이지 study")
    @Test
    void testStudySettingsPageStudy() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/settings/study", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "study"))
                .andExpect(view().name(StudySettingsController.STUDY_SETTINGS_VIEW))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/study/publish, 스터디 공개")
    @Test
    void testStudyPublished() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/study/{path}/settings/study/publish", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/study"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertTrue(study.isPublished());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/study/close, 스터디 종료")
    @Test
    void testStudyClosed() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();
        mockMvc.perform(post("/study/{path}/settings/study/close", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/study"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertTrue(study.isClosed());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/recruit/start, 팀원 모집")
    @Test
    void testRecruitStart() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/study/{path}/settings/recruit/start", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/study"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertTrue(study.isRecruiting());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/study/path, 스터디 경로 변경")
    @Test
    void testUpdateStudyPath() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        String newPath = "한글";
        mockMvc.perform(post("/study/{path}/settings/study/path", StudyFactory.DEFAULT_PATH)
                        .param("newPath", newPath)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + URLEncoder.encode(newPath, StandardCharsets.UTF_8) + "/settings/study"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/study/title, 스터디 제목 변경")
    @Test
    void testUpdateStudyTitle() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        String newTitle = "newTitle";
        mockMvc.perform(post("/study/{path}/settings/study/title", StudyFactory.DEFAULT_PATH)
                        .param("newTitle", newTitle)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/settings/study"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertEquals(newTitle, study.getTitle());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/study/remove, 스터디 삭제")
    @Test
    void testRemoveStudy() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/study/{path}/settings/study/remove", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
        assertThrows(IllegalArgumentException.class, () -> studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager));
    }

    @WithAccount("joinUser")
    @DisplayName("[POST] /study/{path}/join, 스터디 참가")
    @Test
    void testJoinStudy() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(post("/study/{path}/join", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH))
                .andExpect(authenticated().withUsername("joinUser"))
        ;

        Study study = studyFactory.getStudy(StudyFactory.DEFAULT_PATH, manager);
        assertEquals(1, study.getManagers().size());
        assertEquals(1, study.getMembers().size());
    }

    @WithAccount({USER_NAME, "joinUser"})
    @DisplayName("[POST] /study/{path}/leave, 스터디 탈퇴")
    @Test
    void testLeaveStudy() throws Exception {
        Account manager = accountFactory.findAccountByNickname(USER_NAME);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        Account byNickname = accountFactory.findAccountByNickname("joinUser");
        study.join(byNickname);
        mockMvc.perform(post("/study/{path}/leave", StudyFactory.DEFAULT_PATH)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH))
                .andExpect(authenticated().withUsername("joinUser"))
        ;
        assertEquals(1, study.getManagers().size());
        assertEquals(0, study.getMembers().size());
    }
}