package be.shwan.account.presentation;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.domain.Email;
import be.shwan.account.dto.LoginDto;
import be.shwan.account.dto.SignUpRequestDto;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @BeforeEach
    void init() throws Exception {
        SignUpRequestDto dto = SignUpRequestDto.builder()
                .nickname("test")
                .password("password")
                .email("test2@test.com")
                .build();
        accountService.signUp(dto);
    }

    @AfterEach
    void after() {
        accountRepository.deleteAll();
    }

    @DisplayName("[POST] /sign-up 회원 가입 성공 테스트")
    @Test
    void sign_in() throws Exception {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .nickname("testUser")
                .password("password")
                .email("test@gmail.com")
                .build();
        mockMvc.perform(post("/api/auth/sign-up")
//                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("nickname").value("testUser"))
                .andExpect(jsonPath("password").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("email").value("test@gmail.com"))
        ;
    }

    @DisplayName("[POST] /api/auth/login 로그인 성공")
    @Test
    void login() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .usernameOrEmail("test")
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("[POST] /api/auth/login 로그인 실패")
    @Test
    void login_fail() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .usernameOrEmail("test2")
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("[POST] /api/auth/? 잘못된 엔드포인트 접근")
    @Test
    void wrong_endpoint() throws Exception {

        mockMvc.perform(get("/api/auth/login2")
                )
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("[POST] /api/auth/? 엔드포인트는 존재하나 찾는 데이터가 없음")
    @Test
    void wrong_resource() throws Exception {
        mockMvc.perform(get("/api/auth/login2")
                )
                .andExpect(status().isNotFound())
        ;
    }

}