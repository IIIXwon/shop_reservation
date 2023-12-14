package be.shwan.modules.event.presentation.rest;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.config.AppProperties;
import be.shwan.infra.jwt.JwtTokenUtil;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.Notifications;
import be.shwan.modules.event.EventFactory;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.domain.EventType;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.event.event.EnrollmentEventListener;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.notification.domain.NotificationRepository;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.event.StudyEventListener;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static be.shwan.modules.event.EventFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestEventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    EventFactory eventFactory;

    @Autowired
    EventService eventService;

    @Autowired
    TagService tagService;

    @Autowired
    ZoneService zoneService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    Account studyManager;
    String managerToken;

    Account studyMember;
    String memberToken;

    Study defaultStudy;

    Event defaultEvent;

    @BeforeEach
    void init() throws Exception {
        studyManager = accountFactory.createAccount("manager");
        managerToken = appProperties.getTokenPrefix() + " " + jwtTokenUtil.generateToken(studyManager);

        studyManager.addTag(createTag("1"));
        studyManager.addTag(createTag("2"));
        studyManager.addTag(createTag("3"));
        studyManager.addTag(createTag("4"));

        studyManager.addZone(createZone("Andong(안동시)/North Gyeongsang"));
        studyManager.addZone(createZone("Ansan(안산시)/Gyeonggi"));


        studyMember = accountFactory.createAccount("member");
        memberToken = appProperties.getTokenPrefix() + " " + jwtTokenUtil.generateToken(studyMember);

        defaultStudy = studyFactory.defaultTestCreateStudy(studyManager);
        defaultStudy.publish();

        defaultEvent = eventFactory.createDefaultEvent(studyManager, defaultStudy);
    }

    private Zone createZone(String zoneName) {
        return zoneService.findZone(zoneName);
    }

    private Tag createTag(String tagTitle) {
        RequestTagDto tagDto = new RequestTagDto(tagTitle);
        return tagService.getTag(tagDto);
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    @DisplayName("[POST] /api/events/study/{path}/new-event, 스터디 모임 생성")
    @Test
    void newEvent() throws Exception {
        defaultStudy.addZone(createZone("Ansan(안산시)/Gyeonggi"));
        EventRequestDto dto = new EventRequestDto("test2", DEFAULT_EVENT_TYPE, DEFAULT_EVENT_LIMIT_OF_ENROLLMENTS,
                DEFAULT_EVENT_END_ENROLLMENT_DATE_TIME.format(getDateTimeFormatter()),
                DEFAULT_EVENT_START_DATE_TIME.format(getDateTimeFormatter()),
                DEFAULT_EVENT_END_DATE_TIME.format(getDateTimeFormatter()), DEFAULT_EVENT_DESCRIPTION);
        mockMvc.perform(post("/api/events/study/{path}/new-event", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andDo(print())
        ;
        List<Notification> notifications = notificationRepository.findByAccountOrderByCreatedDateTime(studyManager);
        assertEquals(1, notifications.size());
    }

    @DisplayName("[GET] /api/events/study/{path}/events/{eventId}, 스터디 모임 정보 조회")
    @Test
    void getEventInfo() throws Exception {
        mockMvc.perform(get("/api/events/study/{path}/events/{eventId}", StudyFactory.DEFAULT_PATH, defaultEvent.getId())
                        .header(HttpHeaders.AUTHORIZATION, managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.study", " $.event").exists())
                .andDo(print())
        ;
    }

    @DisplayName("[GET] /api/events/study/{path}/events, 스터디 모임 목록 조회")
    @Test
    void getEventList() throws Exception {
        mockMvc.perform(get("/api/events/study/{path}/events", StudyFactory.DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newEvents", " $.oldEvents").exists())
                .andDo(print())
        ;
    }

    @DisplayName("[POST] /api/events/study/{path}/events/{eventId}/edit, 스터디 모임 업데이트")
    @Test
    void updateEvent() throws Exception {
        String title = "test2";
        EventRequestDto dto = new EventRequestDto(title, EventType.CONFIRMATIVE, DEFAULT_EVENT_LIMIT_OF_ENROLLMENTS,
                DEFAULT_EVENT_END_ENROLLMENT_DATE_TIME.format(getDateTimeFormatter()),
                DEFAULT_EVENT_START_DATE_TIME.format(getDateTimeFormatter()),
                DEFAULT_EVENT_END_DATE_TIME.format(getDateTimeFormatter()), "모임 소개 내용이 변경되었습니다.");
        mockMvc.perform(post("/api/events/study/{path}/events/{eventId}/edit", StudyFactory.DEFAULT_PATH, defaultEvent.getId())
                        .header(HttpHeaders.AUTHORIZATION, managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
        ;
        assertEquals(title, defaultEvent.getTitle());
    }

    @DisplayName("[delete] /api/events/study/{path}/events/{eventId}/edit, 스터디 모임 취소")
    @Test
    void deleteEvent() throws Exception {
        mockMvc.perform(delete("/api/events/study/{path}/events/{eventId}", StudyFactory.DEFAULT_PATH, defaultEvent.getId())
                        .header(HttpHeaders.AUTHORIZATION, managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
        ;
    }

    @DisplayName("[POST] /api/events/study/{path}/events/{eventId}/enroll, 모임 참가")
    @Test
    void enrollEvent() throws Exception {
        studyService.join(defaultStudy, studyMember);
        mockMvc.perform(post("/api/events/study/{path}/events/{eventId}/enroll", StudyFactory.DEFAULT_PATH, defaultEvent.getId())
                        .header(HttpHeaders.AUTHORIZATION, memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
        ;
    }

    @DisplayName("[POST] /api/events/study/{path}/events/{eventId}/leave, 모임 참가 취소")
    @Test
    void leaveEvent() throws Exception {
        studyService.join(defaultStudy, studyMember);
        eventService.enrollEvent(defaultEvent, studyMember);
        mockMvc.perform(post("/api/events/study/{path}/events/{eventId}/leave", StudyFactory.DEFAULT_PATH, defaultEvent.getId())
                        .header(HttpHeaders.AUTHORIZATION, memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
        ;
    }
}