package be.shwan.modules.study.presentation.rest;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.config.AppProperties;
import be.shwan.infra.jwt.JwtTokenUtil;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static be.shwan.modules.study.StudyFactory.DEFAULT_PATH;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestStudyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

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

    Account studyMember;

    String memberToken;

    String memberBearerToken;


    @BeforeEach
    void init() throws Exception {
        studyManager = accountFactory.createDefaultAccount();
        managerToken = jwtTokenUtil.generateToken(studyManager);
        managerBearerToken = appProperties.getTokenPrefix() + " " + managerToken;
        studyFactory.defaultTestCreateStudy(studyManager);

        studyMember = accountFactory.createAccount("member");
        memberToken = jwtTokenUtil.generateToken(studyMember);
        memberBearerToken = appProperties.getTokenPrefix() + " " + memberToken;


    }

    @DisplayName("[POST] /api/study, 새로운 스터디 생성")
    @Test
    void newStudy() throws Exception {
        StudyRequestDto request = new StudyRequestDto("DEFAULT_PATH", "DEFAULT_TITLE", "DEFAULT_SHORT_DESCRIPTION", "DEFAULT_FULL_DESCRIPTION");
        String redirect = "redirect:/study/" + URLEncoder.encode(request.path(), StandardCharsets.UTF_8);
        mockMvc.perform(post("/api/study")
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl(redirect))
                .andDo(print())
        ;

        assertEquals(2, studyRepository.findAll().size());
    }

    @DisplayName("[GET] /api/study/{path}, 스터디 조회")
    @Test
    void getStudy() throws Exception {
        mockMvc.perform(get("/api/study/{path}", DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken))
                .andExpect(status().isOk())
                .andDo(print())
        ;

    }

    @DisplayName("[GET] /api/study/{path}/members, 스터디 참가 맴버 조회")
    @Test
    void studyMemberPage() throws Exception {
        mockMvc.perform(get("/api/study/{path}/members", DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, managerBearerToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("[POST] /api/study/{path}/join, 스터디 참가")
    @Test
    void joinStudy() throws Exception {
        Study study = studyFactory.getStudy(DEFAULT_PATH);
        study.publish();
        mockMvc.perform(post("/api/study/{path}/join", DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, memberBearerToken))
                .andExpect(status().isOk())
                .andDo(print())
        ;

        Study byPath = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(1, byPath.getMemberCount());
    }

    @DisplayName("[POST] /api/study/{path}/leave")
    @Test
    void leaveStudy() throws Exception {
        Study study = studyFactory.getStudy(DEFAULT_PATH);
        study.publish();
        studyService.join(study, studyMember);
        mockMvc.perform(post("/api/study/{path}/leave", DEFAULT_PATH)
                        .header(HttpHeaders.AUTHORIZATION, memberBearerToken))
                .andExpect(status().isOk())
                .andDo(print())
        ;

        Study byPath = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(0, byPath.getMemberCount());
    }
}