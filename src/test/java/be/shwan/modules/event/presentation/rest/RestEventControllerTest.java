package be.shwan.modules.event.presentation.rest;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.config.AppProperties;
import be.shwan.infra.jwt.JwtTokenUtil;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.Notifications;
import be.shwan.modules.event.EventFactory;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.event.event.EnrollmentEventListener;
import be.shwan.modules.notification.domain.Notification;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.event.StudyEventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;

import static be.shwan.modules.event.EventFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestEventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    EventFactory eventFactory;

    @Autowired
    EventService eventService;

    @MockBean
    EnrollmentEventListener enrollmentEventListener;

    @MockBean
    StudyEventListener studyEventListener;

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

    @BeforeEach
    void init() throws Exception {
        studyManager = accountFactory.createAccount("manager");
        managerToken = appProperties.getTokenPrefix() + " " +jwtTokenUtil.generateToken(studyManager);

        studyMember = accountFactory.createAccount("member");
        memberToken = appProperties.getTokenPrefix() + " " +jwtTokenUtil.generateToken(studyMember);

        defaultStudy = studyFactory.defaultTestCreateStudy(studyManager);
        defaultStudy.publish();

        eventFactory.createDefaultEvent(studyManager, defaultStudy);
    }

    @Test
    void newEvent() throws Exception {
        Notifications notifications = new Notifications(true, true, true, true,
                true, true);
        studyManager.updateNotification(notifications);
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
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }
}