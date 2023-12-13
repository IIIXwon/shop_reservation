package be.shwan.modules.account.presentation;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.LoginDto;
import be.shwan.modules.account.dto.SignUpFormDto;
import be.shwan.modules.account.dto.SignUpFormValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestAccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    @InitBinder("signUpFormDto")
    void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody @Valid SignUpFormDto signUpFormDto, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Account account = accountService.processNewAccount(signUpFormDto);
        URI uri = URI.create("/");
        return ResponseEntity.created(uri).body("환영합니다. " + account.getNickname() + " 님");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto loginDto, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        String generateToken = accountService.generateToken(loginDto);
        URI uri = URI.create("/");
        return ResponseEntity.created(uri).body(generateToken);
    }
}
