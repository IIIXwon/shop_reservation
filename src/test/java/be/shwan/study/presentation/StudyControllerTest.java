package be.shwan.study.presentation;

import be.shwan.WithAccount;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
    AccountRepository accountRepository;
    @Autowired
    StudyRepository studyRepository;

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
        String path = "testPath";
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
        Account byNickname = accountRepository.findByNickname(USER_NAME);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        Study study = studyService.newStudy(byNickname, studyRequestDto);
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
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        Account byNickname = accountRepository.findByNickname(USER_NAME);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        Study study = studyService.newStudy(byNickname, studyRequestDto);

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
        String testTitle = "testTitle";
        String testShotDescription = "testShotDescription";
        String testFullDescription = "testFullDescription";
        Account byNickname = accountRepository.findByNickname(USER_NAME);
        StudyRequestDto studyRequestDto = new StudyRequestDto(path, testTitle, testShotDescription, testFullDescription);
        Study study = studyService.newStudy(byNickname, studyRequestDto);

        mockMvc.perform(get("/study/{path}/members", path))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/members"))
                .andExpect(authenticated().withUsername(USER_NAME))
        ;
    }
}