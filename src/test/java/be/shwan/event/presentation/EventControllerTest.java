package be.shwan.event.presentation;

import be.shwan.WithAccount;
import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.enrollment.application.EnrollService;
import be.shwan.enrollment.domain.EnrollmentRepository;
import be.shwan.enrollment.domain.Enrollment;
import be.shwan.event.application.EventService;
import be.shwan.event.domain.Event;
import be.shwan.event.domain.EventRepository;
import be.shwan.event.domain.EventType;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class EventControllerTest {
    private final String TEST_USER = "testUser";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyService studyService;
    @Autowired
    StudyRepository studyRepository;

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EnrollService enrollService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void init() {
        String path = "testPath";
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        Study study = studyService.newStudy(byNickname, studyRequestDto);
        studyService.publish(study);
    }

    @AfterEach
    void afterEach() {
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @WithAccount(TEST_USER)
    @DisplayName("[GET] /study/{path}/new-event, 모임 생성 페이지")
    @Test
    void testEventForm() throws Exception {
        mockMvc.perform(get("/study/{path}/new-event", "testPath"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/form"))
                .andExpect(model().attributeExists("account", "study", "eventRequestDto"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/new-event, 모임 생성")
    @Test
    void testNewEvent() throws Exception {
        String path = "testPath";

        mockMvc.perform(post("/study/{path}/new-event", path)
                        .param("title", "test")
                        .param("eventType", EventType.CONFIRMATIVE.toString())
                        .param("limitOfEnrollments", "2")
                        .param("endEnrollmentDateTime", "2023-12-07T12:44:00")
                        .param("startDateTime", "2023-12-08T00:00:00")
                        .param("endDateTime", "2023-12-31T23:59:59")
                        .param("description", "")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/1"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[GET] /study/{path}/events/{id}, 모임 상세 페이지")
    @Test
    void testEventViewPage() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(byNickname, study, requestDto);
        mockMvc.perform(get("/study/{path}/events/{id}", path, 1)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("events/view"))
                .andExpect(model().attributeExists("account", "study", "event"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[GET] /study/{path}/events, 모임 목록 페이지")
    @Test
    void testEventListPage() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/events", path)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("study/events"))
                .andExpect(model().attributeExists("account", "newEvents", "oldEvents"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[GET] /study/{path}/events/{id}/edit, 모임 수정 페이지")
    @Test
    void testUpdateEventFormPage() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(byNickname, study, requestDto);
        mockMvc.perform(get("/study/{path}/events/{id}/edit", path, 1)
                )
                .andExpect(status().isOk())
                .andExpect(view().name(EventController.EVENT_UPDATE_FORM_VIEW))
                .andExpect(model().attributeExists("account", "study", "eventRequestDto"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/edit, 모임 수정")
    @Test
    void testUpdateEvent() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(byNickname, study, requestDto);

        mockMvc.perform(post("/study/{path}/events/{id}/edit", path, 1)
                        .param("title", "한글")
                        .param("eventType", EventType.CONFIRMATIVE.toString())
                        .param("limitOfEnrollments", "5")
                        .param("endEnrollmentDateTime", "2023-12-07T12:44:00")
                        .param("startDateTime", "2023-12-08T00:00:00")
                        .param("endDateTime", "2023-12-31T23:59:59")
                        .param("description", "")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/1"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[DELETE] /study/{path}/events/{id}, 모임 삭제")
    @Test
    void testDeleteEvent() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(byNickname, study, requestDto);
        mockMvc.perform(delete("/study/{path}/events/{id}", path, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/enroll, 모임 참가")
    @Test
    void testEnrollEvent() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.FCFS, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        Event event = eventService.createEvent(byNickname, study, requestDto);

        mockMvc.perform(post("/study/{path}/events/{id}/enroll", path, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/1"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;

        Enrollment enrollment = enrollmentRepository.findEnrollmentWithEventAndAccountByEventAndAccount(event, byNickname);
        assertEquals(byNickname, enrollment.getAccount());
        assertEquals(event, enrollment.getEvent());
        assertEquals(1, enrollment.getId());
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/leave, 모임 참가 취소")
    @Test
    void testLeaveEvent() throws Exception {
        String path = "testPath";
        Study study = studyService.getStudy(path);
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.FCFS, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        Event event = eventService.createEvent(byNickname, study, requestDto);
        eventService.enrollEvent(event, byNickname, study);
        mockMvc.perform(post("/study/{path}/events/{id}/leave", path, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/1"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;

        List<Enrollment> all = enrollmentRepository.findAll();
        assertEquals(0, all.size());
    }
}