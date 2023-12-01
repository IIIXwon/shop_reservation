package be.shwan.account.domain;

import be.shwan.config.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository repository;

    @BeforeEach
    void init() throws Exception {
        Account account = new Account("testUser", "123456","test1@test.com");
        repository.save(account);
    }
    @Test
    void save() throws Exception {
        Account account = new Account("testUser2", "123456", "test@test.com");
        Account save = repository.save(account);
        assertEquals(2L, save.getId());
    }

    @Test
    void findByFirstNameAndLastName() {
        Account account = repository.findByNickname("testUser");
        assertNotNull(account);
    }
}