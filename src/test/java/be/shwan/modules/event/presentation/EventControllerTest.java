package be.shwan.modules.event.presentation;

import be.shwan.infra.AbstractContainerBaseTest;
import be.shwan.infra.MockMvcTest;
import be.shwan.infra.mail.dto.EmailMessage;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.WithAccount;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.EventFactory;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.domain.EventType;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.event.event.EnrollmentEvent;
import be.shwan.modules.event.event.EnrollmentEventListener;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class EventControllerTest extends AbstractContainerBaseTest {
    private final String TEST_USER = "testUser";
    private final String ADMIN = "admin";
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
    EnrollmentEventListener eventListener;

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/new-event, 모임 생성 페이지")
    @Test
    void testEventForm() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        studyFactory.defaultTestCreateStudy(manager);

        mockMvc.perform(get("/study/{path}/new-event", StudyFactory.DEFAULT_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EventController.EVENT_FORM_VIEW))
                .andExpect(model().attributeExists("account", "study", "eventRequestDto"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/new-event, 모임 생성")
    @Test
    void testNewEvent() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        mockMvc.perform(post("/study/{path}/new-event", StudyFactory.DEFAULT_PATH)
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
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/events/{id}, 모임 상세 페이지")
    @Test
    void testEventViewPage() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        Event event = eventFactory.createDefaultEvent(manager, study);

        mockMvc.perform(get("/study/{path}/events/{id}", StudyFactory.DEFAULT_PATH, event.getId())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(EventController.EVENT_VIEW))
                .andExpect(model().attributeExists("account", "study", "event"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/events, 모임 목록 페이지")
    @Test
    void testEventListPage() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        mockMvc.perform(get("/study/{path}/events", StudyFactory.DEFAULT_PATH)
                )
                .andExpect(status().isOk())
                .andExpect(view().name(EventController.STUDY_EVENT_VIEW))
                .andExpect(model().attributeExists("account", "newEvents", "oldEvents"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[GET] /study/{path}/events/{id}/edit, 모임 수정 페이지")
    @Test
    void testUpdateEventFormPage() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        Event event = eventFactory.createDefaultEvent(manager, study);

        mockMvc.perform(get("/study/{path}/events/{id}/edit", StudyFactory.DEFAULT_PATH, event.getId())
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
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        Event event = eventFactory.createDefaultEvent(manager, study);

        mockMvc.perform(post("/study/{path}/events/{id}/edit", StudyFactory.DEFAULT_PATH, event.getId())
                        .param("title", "한글")
                        .param("eventType", EventType.FCFS.toString())
                        .param("limitOfEnrollments", "5")
                        .param("endEnrollmentDateTime", EventFactory.DEFAULT_EVENT_END_ENROLLMENT_DATE_TIME.format(EventFactory.getDateTimeFormatter()))
                        .param("startDateTime", EventFactory.DEFAULT_EVENT_START_DATE_TIME.format(EventFactory.getDateTimeFormatter()))
                        .param("endDateTime", EventFactory.DEFAULT_EVENT_END_DATE_TIME.format(EventFactory.getDateTimeFormatter()))
                        .param("description", EventFactory.DEFAULT_EVENT_DESCRIPTION)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/events/" + event.getId()))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
    }

    @WithAccount(ADMIN)
    @DisplayName("[DELETE] /study/{path}/events/{id}, 모임 삭제")
    @Test
    void testDeleteEvent() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        Event event = eventFactory.createDefaultEvent(manager, study);

        mockMvc.perform(delete("/study/{path}/events/{id}", StudyFactory.DEFAULT_PATH, event.getId())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/events"))
                .andExpect(authenticated().withUsername(ADMIN))
        ;
        assertThrows(NoSuchElementException.class, () -> eventFactory.findEventById(1L));
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/enroll, 모임 참가")
    @Test
    void testEnrollEvent() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        Event newEvent = eventFactory.createDefaultEvent(manager, study);

        mockMvc.perform(post("/study/{path}/events/{id}/enroll", StudyFactory.DEFAULT_PATH, newEvent.getId())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/events/" + newEvent.getId()))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;

        Event event = eventFactory.findEventById(newEvent.getId());
        assertEquals(1, event.getEnrollments().size());
    }

    @WithAccount(TEST_USER)
    @DisplayName("[POST] /study/{path}/events/{id}/leave, 모임 참가 취소")
    @Test
    void testLeaveEvent() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        Event event = eventFactory.createDefaultEvent(manager, study);
        Account testUser = accountFactory.findAccountByNickname(TEST_USER);
        eventService.enrollEvent(event, testUser);

        mockMvc.perform(post("/study/{path}/events/{id}/leave", StudyFactory.DEFAULT_PATH, event.getId())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/events/" + event.getId()))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;

        Event byId = eventFactory.findEventById(event.getId());
        assertEquals(0, byId.getEnrollments().size());
    }

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/events/{id}/enrollments/{enrollmentId}/accept, 모임 참가 승인")
    @Test
    void testAcceptEvent() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 10,
                "2023-12-10T12:44:00", "2023-12-11T12:44:00", "2023-12-31T12:44:00", "");
        Event event = eventFactory.createEvent(manager, study,requestDto);
        Account testUser = accountFactory.findAccountByNickname(TEST_USER);
        Enrollment enrollment = eventService.enrollEvent(event, testUser);

        mockMvc.perform(get("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/accept",
                        StudyFactory.DEFAULT_PATH, event.getId(), enrollment.getId())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/events/" + event.getId()))
                .andExpect(authenticated().withUsername(ADMIN))
        ;

        Event byId = eventFactory.findEventById(event.getId());
        assertEquals(1, byId.getEnrollments().size());

        then(eventListener).should().enrollmentEventHandler(any(EnrollmentEvent.class));
    }

    @WithAccount(ADMIN)
    @DisplayName("[POST] /study/{path}/events/{id}/enrollments/{enrollmentId}/reject, 모임 참가 승인 취소")
    @Test
    void testRejectEvent() throws Exception {
        Account manager = accountFactory.findAccountByNickname(ADMIN);
        Study study = studyFactory.defaultTestCreateStudy(manager);
        study.publish();

        EventRequestDto requestDto = new EventRequestDto("test", EventType.CONFIRMATIVE, 10,
                "2023-12-10T12:44:00", "2023-12-11T12:44:00", "2023-12-31T12:44:00", "");
        Event event = eventFactory.createEvent(manager, study,requestDto);
        Account testUser = accountFactory.findAccountByNickname(TEST_USER);
        Enrollment enrollment = eventService.enrollEvent(event, testUser);


        mockMvc.perform(get("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/reject",
                        StudyFactory.DEFAULT_PATH, event.getId(), enrollment.getId())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + StudyFactory.DEFAULT_PATH + "/events/" + event.getId()))
                .andExpect(authenticated().withUsername(ADMIN))
        ;

        Enrollment newEnrollment = event.getEnrollments().get(0);
        assertFalse(newEnrollment.isAccepted());

        then(eventListener).should().enrollmentEventHandler(any(EnrollmentEvent.class));
    }
}