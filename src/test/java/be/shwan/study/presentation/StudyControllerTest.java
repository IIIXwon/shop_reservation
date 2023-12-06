package be.shwan.study.presentation;

import be.shwan.WithAccount;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyRequestDto;
import be.shwan.tag.application.TagService;
import be.shwan.tag.dto.RequestTagDto;
import be.shwan.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class StudyControllerTest {
    private final String USER_NAME = "seunghwan";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyService studyService;

    @Autowired
    TagService tagService;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    StudyRepository studyRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        String path = "testPath";
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        Account byNickname = accountRepository.findByNickname(USER_NAME);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        studyService.newStudy(byNickname, studyRequestDto);
    }
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        studyRepository.deleteAll();
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /new-study, 스터디 개설 페이지")
    @Test
    void testStudyFormPage() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studyRequestDto"))
                .andExpect(view().name("study/form"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /new-study, 스터디 개설")
    @Test
    void testNewStudy() throws Exception {
        String path = "testPath2";
        mockMvc.perform(post("/new-study")
                        .param("path", path)
                        .param("title", "testTitle")
                        .param("shortDescription", "testShotDescription")
                        .param("fullDescription", "testFullDescription")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(redirectedUrl("/study/" + path))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        assertTrue(studyRepository.existsByPath(path));
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /new-study, 스터디 개설 실패")
    @Test
    void testNewStudy_fail() throws Exception {
        String path = "testPath";
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        mockMvc.perform(post("/new-study")
                        .param("path", path)
                        .param("title", testTitle)
                        .param("shortDescription", testShotDescription)
                        .param("fullDescription", testFullDescription)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("study/form"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}, 스터디 상세 페이지")
    @Test
    void testStudyViewPage() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}", path))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "study"))
                .andExpect(view().name("study/view"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/members, 스터디 참가자 페이지")
    @Test
    void testStudyMemberPage() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/members", path))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/members"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/description, 스터디 수정 페이지 description")
    @Test
    void testStudySettingsPageDescription() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/settings/description", path))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study", "studyDescriptionRequestDto"))
                .andExpect(view().name("study/settings/description"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/description, 스터디 수정 페이지 description")
    @Test
    void testUpdateStudyDescription() throws Exception {
        String path = "testPath";
        String shortDescription = "한글";
        String fullDescription = "한글로 바꿨습니다";
        mockMvc.perform(post("/study/{path}/settings/description", path)
                        .param("shortDescription", shortDescription)
                        .param("fullDescription", fullDescription)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/settings/description"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study byPath = studyRepository.findByPath(path);
        assertEquals(shortDescription, byPath.getShortDescription());
        assertEquals(fullDescription, byPath.getFullDescription());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/banner, 스터디 수정 페이지 banner")
    @Test
    void testStudySettingsPageBanner() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/settings/banner", path))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/settings/banner"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/banner/enable, 스터디 수정 banner enable")
    @Test
    void testUpdateStudyEnableBanner() throws Exception {
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/settings/banner/enable", path)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/settings/banner"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study byPath = studyRepository.findByPath(path);
        assertTrue(byPath.isUseBanner());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/banner/disable, 스터디 수정 banner disable")
    @Test
    void testUpdateStudyDisableBanner() throws Exception {
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/settings/banner/disable", path)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/settings/banner"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study byPath = studyRepository.findByPath(path);
        assertFalse(byPath.isUseBanner());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/tags, 스터디 수정 페이지 tag")
    @Test
    void testStudySettingsPageTags() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/settings/tags", path))
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
        RequestTagDto requestTagDto = new RequestTagDto("game");
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/settings/tags/add", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyService.getStudy(path);
        assertEquals(1, study.getTags().size());
    }


    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/tags/remove, 스터디 tag 제거")
    @Test
    void testStudyTagsRemove() throws Exception {
        RequestTagDto requestTagDto = new RequestTagDto("game");
        tagService.getTag(requestTagDto);
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/settings/tags/remove", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTagDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyService.getStudy(path);
        assertEquals(0, study.getTags().size());
    }

    @WithAccount(USER_NAME)
    @DisplayName("[GET] /study/{path}/settings/zones, 스터디 수정 페이지 zone")
    @Test
    void testStudySettingsPageZones() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/settings/zones", path))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("whitelist", "study" , "zones"))
                .andExpect(view().name("study/settings/zones"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }

    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/zones/add, 스터디 zone 추가")
    @Test
    void testStudyZonesAdd() throws Exception {
        RequestZoneDto requestZoneDto = new RequestZoneDto("Andong(안동시)/North Gyeongsang");
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/settings/zones/add", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        Study study = studyService.getStudy(path);
        assertEquals(1, study.getZones().size());
    }


    @WithAccount(USER_NAME)
    @DisplayName("[POST] /study/{path}/settings/zones/remove, 스터디 zone 제거")
    @Test
    void testStudyZonesRemove() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        RequestZoneDto requestZoneDto = new RequestZoneDto("Andong(안동시)/North Gyeongsang");
        studyService.addZone(study, requestZoneDto);
        mockMvc.perform(post("/study/{path}/settings/zones/remove", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestZoneDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(USER_NAME))
        ;

        assertEquals(0, study.getZones().size());
    }


}