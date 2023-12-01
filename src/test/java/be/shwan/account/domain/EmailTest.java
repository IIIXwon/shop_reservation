package be.shwan.account.domain;

import be.shwan.account.domain.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {
    @Test
    @DisplayName("Email 객체 생성 성공")
    void successEmail() {
        assertDoesNotThrow(() ->  Email.create("test@test.com"));
    }

    @Test
    @DisplayName("Email 객체 생성 실패")
    void failEmail(){
        assertThrows(IllegalArgumentException.class, () ->
                Email.create("test@")
        );

    }
}