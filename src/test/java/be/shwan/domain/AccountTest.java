package be.shwan.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    @DisplayName("Account 객체 생성 성공")
    void initAccount() {
        assertDoesNotThrow(() -> {
            new Account("원", "승환", new Email("test", "test.com")); });
    }

}