package be.shwan.modules.study.presentation.rest;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.config.AppProperties;
import be.shwan.infra.jwt.JwtTokenUtil;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyDescriptionRequestDto;
import be.shwan.modules.study.dto.StudyPathRequestDto;
import be.shwan.modules.study.dto.StudyTitleRequestDto;
import be.shwan.modules.study.event.StudyEventListener;
import be.shwan.modules.study.event.StudyUpdatedEvent;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestStudySettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    TagService tagService;

    @Autowired
    ZoneService zoneService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    Account studyManager;
    String managerToken;
    String managerBearerToken;
    Study defaultStudy;


    @BeforeEach
    void init() throws Exception {
        studyManager = accountFactory.createDefaultAccount();
        managerToken = jwtTokenUtil.generateToken(studyManager);
        managerBearerToken = appProperties.getTokenPrefix() + " " + managerToken;
        defaultStudy = studyFactory.defaultTestCreateStudy(studyManager);

    }

    @DisplayName("[GET] /api/study/{path}/settings/info, 스터디 매니저가 접근 할 수 있는 데이터 한번에 조회")
    @Test
    void getStudyDescription() throws Exception {
        mockMvc.perform(get("/api/study/{path}/settings/info", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("[POST] /api/study/{path}/settings/description, 스터디 소개 수정")
    @Test
    void updateStudyDescription() throws Exception {
        StudyDescriptionRequestDto dto = new StudyDescriptionRequestDto("히히변경했지롱", "한글이잘나오나요?");
        mockMvc.perform(post("/api/study/{path}/settings/description", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());

        List<Notification> byAccountOrderByCreatedDateTime = notificationRepository.findByAccountOrderByCreatedDateTime(studyManager);
        assertEquals(1, byAccountOrderByCreatedDateTime.size());
    }

    @DisplayName("[POST] /api/study/{path}/settings/banner, 스터디 배너 수정")
    @Test
    void studyBannerImageUpdate() throws Exception {
        Map<String, Object> request = new HashMap<>();
        String image = "jhvlkdshklvsdhklhsakldh";
        request.put("image", image);
        mockMvc.perform(post("/api/study/{path}/settings/banner", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(image, defaultStudy.getImage());
    }

    @DisplayName("[POST] /api/study/{path}/settings/banner/enable, 스터디 배너 사용")
    @Test
    void enableBanner() throws Exception {
        mockMvc.perform(post("/api/study/{path}/settings/banner/enable", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertTrue(defaultStudy.isUseBanner());
    }

    @DisplayName("[POST] /api/study/{path}/settings/banner/disable, 스터디 배너 미사용")
    @Test
    void disableBanner() throws Exception {
        mockMvc.perform(post("/api/study/{path}/settings/banner/disable", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertFalse(defaultStudy.isUseBanner());
    }

    @DisplayName("[GET] /api/study/{path}/settings/tags, tag 화이트 리스트 가져오기")
    @Test
    void tagPage() throws Exception {
        mockMvc.perform(get("/api/study/{path}/settings/tags", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whitelist").exists())
                .andDo(print());
    }

    @DisplayName("[POST] /api/study/{path}/settings/tags/add, 스터디에서 관심있게 다루는 주제 추가")
    @Test
    void addTag() throws Exception {
        RequestTagDto dto = new RequestTagDto("게임");
        mockMvc.perform(post("/api/study/{path}/settings/tags/add", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(1, defaultStudy.getTags().size());
    }

    @DisplayName("[POST] /api/study/{path}/settings/tags/remove, 스터디에서 관심있게 다루는 주제 추가")
    @Test
    void removeTag() throws Exception {
        RequestTagDto dto = new RequestTagDto("게임");
        tagService.getTag(dto);
        mockMvc.perform(post("/api/study/{path}/settings/tags/remove", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(0, defaultStudy.getTags().size());
    }

    @DisplayName("[GET] /api/study/{path}/settings/zones, zone 화이트 리스트 가져오기")
    @Test
    void zonePage() throws Exception {
        mockMvc.perform(get("/api/study/{path}/settings/zones", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whitelist").exists())
                .andDo(print());
    }

    @DisplayName("[POST] /api/study/{path}/settings/zones/add, 스터디 활동지역 추가")
    @Test
    void addZone() throws Exception {
        RequestZoneDto dto = new RequestZoneDto("Andong(안동시)/North Gyeongsang");
        mockMvc.perform(post("/api/study/{path}/settings/zones/add", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(1, defaultStudy.getZones().size());
    }

    @DisplayName("[POST] /api/study/{path}/settings/zones/remove, 스터디 활동지역 제거")
    @Test
    void removeZone() throws Exception {
        RequestZoneDto dto = new RequestZoneDto("Andong(안동시)/North Gyeongsang");
        studyService.addZone(defaultStudy, dto);
        assertEquals(1, defaultStudy.getZones().size());

        mockMvc.perform(post("/api/study/{path}/settings/zones/remove", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(0, defaultStudy.getZones().size());
    }

    @DisplayName("[POST] /api/study/{path}/settings/publish, 스터디 공개 draft -> open")
    @Test
    void studyPublish() throws Exception {
        assertFalse(defaultStudy.isPublished());
        mockMvc.perform(post("/api/study/{path}/settings/publish", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertTrue(defaultStudy.isPublished());
    }

    @DisplayName("[POST] /api/study/{path}/settings/close, 스터디 종료")
    @Test
    void studyClose() throws Exception {
        defaultStudy.publish();
        assertFalse(defaultStudy.isClosed());
        mockMvc.perform(post("/api/study/{path}/settings/close", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertTrue(defaultStudy.isClosed());
    }

    @DisplayName("[POST] /api/study/{path}/settings/recruit/start, 스터디 팀원 모집")
    @Test
    void startRecruiting() throws Exception {
        assertFalse(defaultStudy.isRecruiting());
        mockMvc.perform(post("/api/study/{path}/settings/recruit/start", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertTrue(defaultStudy.isRecruiting());
    }

    @DisplayName("[POST] /api/study/{path}/settings/recruit/stop, 스터디 팀원 모집 중지")
    @Test
    void stopRecruiting() throws Exception {
        defaultStudy.startRecruit();
        assertTrue(defaultStudy.isRecruiting());
        mockMvc.perform(post("/api/study/{path}/settings/recruit/stop", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        assertFalse(defaultStudy.isRecruiting());
    }

    @DisplayName("[POST] /api/study/{path}/settings/path, 스터디 path 수정")
    @Test
    void updateStudyPath() throws Exception {
        String newPath = "new_pathhhh";
        StudyPathRequestDto dto = new StudyPathRequestDto(newPath);
        mockMvc.perform(post("/api/study/{path}/settings/path", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
        Study study = studyRepository.findStudyOnlyByPath(newPath);
        assertEquals(defaultStudy, study);
    }

    @DisplayName("[POST] /api/study/{path}/settings/title, 스터디 제목 수정")
    @Test
    void updateStudyTitle() throws Exception {
        String newTitle = "새로운 제목";
        StudyTitleRequestDto dto = new StudyTitleRequestDto(newTitle);
        mockMvc.perform(post("/api/study/{path}/settings/title", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
        Study study = studyRepository.findStudyOnlyByPath(StudyFactory.DEFAULT_PATH);
        assertEquals(newTitle, study.getTitle());
    }

    @DisplayName("[delete] /api/study/{path}/settings, 스터디 삭제")
    @Test
    void removeStudy() throws Exception {
        mockMvc.perform(delete("/api/study/{path}/settings", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Study study = studyRepository.findStudyOnlyByPath(StudyFactory.DEFAULT_PATH);
        assertNull(study);
    }
}