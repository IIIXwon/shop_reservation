package be.shwan.event.presentation;

import be.shwan.WithAccount;
import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.event.domain.EventType;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyRequestDto;
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

    @BeforeEach
    void init() {
        String path = "testPath";
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        Account byNickname = accountRepository.findByNickname(TEST_USER);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        studyService.newStudy(byNickname, studyRequestDto);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        studyRepository.deleteAll();
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
        Study study = studyService.getStudy(path);
        studyService.publish(study);

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
                .andExpect(redirectedUrl("/study/" + path))
                .andExpect(authenticated().withUsername(TEST_USER))
        ;
    }
}