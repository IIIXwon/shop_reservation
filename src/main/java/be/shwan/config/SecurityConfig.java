package be.shwan.config;

import be.shwan.account.application.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import javax.sql.DataSource;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AccountService accountService) throws Exception {
        http.authorizeHttpRequests((authorize) ->
                authorize
                        // api
                        .requestMatchers("/", "/login", "/sign-up", "/check-email-token",
                                "/email-login", "/check-email-login", "login-link").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/**").permitAll()
                        // web
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/node_modules/**").permitAll()
                        .anyRequest().authenticated()
        );

        http.
                rememberMe((rememberMe) ->
                        rememberMe.userDetailsService(accountService).tokenRepository(tokenRepository()));

        http
                .securityContext((securityContext) -> securityContext .requireExplicitSave(false));

        http.formLogin((formLogin) -> {
            formLogin.loginPage("/login").permitAll();
        });

        http.logout((logout) -> {
            logout.logoutSuccessUrl("/");
        });

        return http.build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }
}
