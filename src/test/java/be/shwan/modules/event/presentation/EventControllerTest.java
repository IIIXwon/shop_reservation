package be.shwan.modules.event.presentation;

import be.shwan.modules.account.WithAccount;
import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.dto.SignUpFormDto;
import be.shwan.modules.event.application.EnrollService;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.EnrollmentRepository;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.domain.EventRepository;
import be.shwan.modules.event.domain.EventType;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private final String ADMIN = "admin";
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
    void init() throws Exception {
        String path = "testPath";
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        Account byNickname = getAccount(ADMIN);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        Study study = studyService.newStudy(byNickname, studyRequestDto);
        studyService.publish(study);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.FCFS, 2,
                "2023-12-07T12:44:00", "2023-12-08T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(byNickname, study, requestDto);
    }



    @AfterEach
    void afterEach() {
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/new-event, 모임 생성 페이지")
    @Test
    void testEventForm() throws Exception {
        mockMvc.perform(get("/study/{path}/new-event", "testPath"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/form"))
                .andExpect(model().attributeExists("account", "study", "eventRequestDto"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/new-event, 모임 생성")
    @Test
    void testNewEvent() throws Exception {
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/new-event", path)
                        .param("title", "test")
                        .param("eventType", EventType.FCFS.toString())
                        .param("limitOfEnrollments", "2")
                        .param("endEnrollmentDateTime", "2023-12-07T12:44:00")
                        .param("startDateTime", "2023-12-08T00:00:00")
                        .param("endDateTime", "2023-12-31T23:59:59")
                        .param("description", "")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/2"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/events/{id}, 모임 상세 페이지")
    @Test
    void testEventViewPage() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/events/{id}", path, 1)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("events/view"))
                .andExpect(model().attributeExists("account", "study", "event"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/events, 모임 목록 페이지")
    @Test
    void testEventListPage() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/events", path)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("study/events"))
                .andExpect(model().attributeExists("account", "newEvents", "oldEvents"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/events/{id}/edit, 모임 수정 페이지")
    @Test
    void testUpdateEventFormPage() throws Exception {
        String path = "testPath";
        mockMvc.perform(get("/study/{path}/events/{id}/edit", path, 1)
                )
                .andExpect(status().isOk())
                .andExpect(view().name(EventController.EVENT_UPDATE_FORM_VIEW))
                .andExpect(model().attributeExists("account", "study", "eventRequestDto"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/events/{id}/edit, 모임 수정")
    @Test
    void testUpdateEvent() throws Exception {
        String path = "testPath";
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
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[DELETE] /study/{path}/events/{id}, 모임 삭제")
    @Test
    void testDeleteEvent() throws Exception {
        String path = "testPath";
        mockMvc.perform(delete("/study/{path}/events/{id}", path, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/enroll, 모임 참가")
    @Test
    void testEnrollEvent() throws Exception {
        String path = "testPath";
        mockMvc.perform(post("/study/{path}/events/{id}/enroll", path, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/1"))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;

        List<Enrollment> all = enrollmentRepository.findAll();
        assertEquals(1, all.size());
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/leave, 모임 참가 취소")
    @Test
    void testLeaveEvent() throws Exception {
        String path = "testPath";
        Event event = eventRepository.findById(1L).orElseThrow();
        Account account = accountRepository.findByNickname(TEST_USER);
        eventService.enrollEvent(event, account);
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

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/events/{id}/enrollments/{enrollmentId}/accept, 모임 참가 승인")
    @Test
    void testAcceptEvent() throws Exception {
        String path = "testPath";
        Study study = studyRepository.findStudyWithMembersAndManagersByPath(path);
        Account admin = getAccount(ADMIN);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 10,
                "2023-12-10T12:44:00", "2023-12-11T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(admin, study, requestDto);
        Event event = eventRepository.findById(2L).orElseThrow();
        Account account = getAccount(TEST_USER);
        eventService.enrollEvent(event, account);
        mockMvc.perform(post("/study/{path}/events/{id}/enrollments/{enrollmentId}/accept", path, 2, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/2"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;

        List<Enrollment> all = enrollmentRepository.findAll();
        assertEquals(1, all.size());
    }

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/events/{id}/enrollments/{enrollmentId}/reject, 모임 참가 승인 취소")
    @Test
    void testRejectEvent() throws Exception {
        String path = "testPath";
        Study study = studyRepository.findStudyWithMembersAndManagersByPath(path);
        Account admin = getAccount(ADMIN);
        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 10,
                "2023-12-10T12:44:00", "2023-12-11T12:44:00", "2023-12-31T12:44:00", "");
        eventService.createEvent(admin, study, requestDto);
        Event event = eventRepository.findById(2L).orElseThrow();
        Account account = getAccount(TEST_USER);
        eventService.enrollEvent(event, account);
        mockMvc.perform(post("/study/{path}/events/{id}/enrollments/{enrollmentId}/reject", path, 2, 1)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + path + "/events/2"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;

        List<Enrollment> all = event.getEnrollments();
        assertEquals(0, all.size());
    }

    private Account getAccount(String nickname) throws Exception {
        Account byNickname = accountRepository.findByNickname(nickname);
        if( byNickname == null ) {
            byNickname = accountService.processNewAccount(new SignUpFormDto(nickname, "admin@admin.com", "12345678"));
        }
        return byNickname;
    }


}