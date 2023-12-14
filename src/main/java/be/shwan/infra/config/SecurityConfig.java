package be.shwan.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http.authorizeHttpRequests((authorize) ->
                        authorize
// api
                                .requestMatchers("/", "/login", "/sign-up", "/check-email-token",
                                        "/email-login", "/check-email-login", "/login-by-email").permitAll()
                                .requestMatchers(HttpMethod.GET, "/profile/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/search/study").permitAll()
// web
                                .requestMatchers(HttpMethod.GET, "/images/**", "/node_modules/**").permitAll()
                                .anyRequest().authenticated()
        );

        http.
                rememberMe((rememberMe) ->
                        rememberMe.userDetailsService(userDetailsService).tokenRepository(tokenRepository()));

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

    private List<String> getResourcePath() {
        return Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .collect(Collectors.toList());
    }
}