package be.shwan.modules.main.rest;

import be.shwan.infra.MockMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestMainControllerTest {
    @Autowired
    MockMvc mockMvc;

    @DisplayName("[GET] /, 메인 정보 불러오기 비로그인 시")
    @Test
    void mainPage() throws  Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studyList").isArray())
                .andDo(print())
                ;

    }
}