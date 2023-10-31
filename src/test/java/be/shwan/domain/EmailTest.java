package be.shwan.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {
    @Test
    @DisplayName("Email 객체 생성 성공")
    void successEmail() {
        assertDoesNotThrow(() -> new Email("test", "test.com"));
    }
}