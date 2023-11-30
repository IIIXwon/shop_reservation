package be.shwan.config;

import be.shwan.account.domain.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

//@RepositoryTest
public class TestRepositoryConfig {

    @PersistenceContext
    private EntityManager entityManager;


    @Bean
    public SimpleJpaRepository accountRepository() {
        return new SimpleJpaRepository<>(Account.class, entityManager);
    }

}

