package be.shwan.infra.config;

import be.shwan.modules.account.domain.Account;
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

