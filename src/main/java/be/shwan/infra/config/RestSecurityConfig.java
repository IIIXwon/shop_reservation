package be.shwan.infra.config;

import be.shwan.infra.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class RestSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize
                                // api
                                .requestMatchers("/", "/login", "/sign-up", "/check-email-token",
                                        "/email-login", "/check-email-login", "/login-by-email").permitAll()
                                .requestMatchers(HttpMethod.GET, "/profile/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/search/study").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                                // web
                                .requestMatchers(HttpMethod.GET, "/images/**", "/node_modules/**").permitAll()
                                .anyRequest().authenticated())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}
