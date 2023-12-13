package be.shwan.modules.account.presentation;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.jwt.JwtTokenUtil;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.LoginDto;
import be.shwan.modules.account.dto.SignUpFormDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class RestAccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("[POST] /api/signup, 회원가입 실패")
    @Test
    void signUpFailed() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes("")))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("[POST] /api/signup, 회원가입 성공")
    @Test
    void signUpSuccess() throws Exception {
        SignUpFormDto dto = new SignUpFormDto(AccountFactory.DEFAULT_ACCOUNT_NAME, AccountFactory.DEFAULT_ACCOUNT_EMAIL,
                AccountFactory.DEFAULT_ACCOUNT_PASSWORD);
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/"));
    }

    @DisplayName("[POST] /api/login, 로그인 시도 성공시, accessToken 발급")
    @Test
    void loginSuccess() throws Exception {
        Account account = accountFactory.createAccount(AccountFactory.DEFAULT_ACCOUNT_NAME);
        LoginDto dto = new LoginDto(account.getEmail(), AccountFactory.DEFAULT_ACCOUNT_PASSWORD);
        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/"))
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());
    }

    @DisplayName("[GET] /api/profiles, 자신의 프로필 정보 불러오기")
    @Test
    void profileInfoSuccess() throws Exception {
        Account account = accountFactory.createAccount(AccountFactory.DEFAULT_ACCOUNT_NAME);
        String token = jwtTokenUtil.generateToken(account);
        MvcResult mvcResult = mockMvc.perform(get("/api/profiles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse().getContentAsString());
    }
}