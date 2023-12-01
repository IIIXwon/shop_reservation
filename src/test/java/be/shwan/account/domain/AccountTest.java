package be.shwan.account.domain;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    @DisplayName("Account 객체 생성 성공")
    void successAccount() {
        assertDoesNotThrow(() -> {
            new Account("testUser", "123456", "test@test.com"); });
    }

    @Test
    @DisplayName("Account 객체 생성 성공")
    void failAccount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Account("testUser", "123456", "test@test.com"); });
    }

}