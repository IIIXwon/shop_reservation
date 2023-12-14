package be.shwan.modules.main.rest;

import be.shwan.infra.MockMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestSearchControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void getStudyList() throws Exception {
        mockMvc.perform(get("/api/study").param("keyword", "study"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account", "$.studyPage", "$.keyword", "$.sortProperty").exists())
                .andDo(print());

    }
}