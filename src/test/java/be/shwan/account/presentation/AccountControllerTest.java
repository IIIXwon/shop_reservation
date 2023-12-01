package be.shwan.account.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("[GET] /sign-up 회원가입 페이지 접근")
    @Test
    void testSignUpPage() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("accounts/sign-up"))
                .andExpect(status().isOk());

    }
}