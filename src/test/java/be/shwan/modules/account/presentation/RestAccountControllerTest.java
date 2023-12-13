package be.shwan.modules.account.presentation;

import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.dto.SignUpFormDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class RestAccountControllerTest {
    @Autowired
    MockMvc mockMvc;

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
}