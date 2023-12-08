package be.shwan.modules.account.presentation;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.LoginDto;
import be.shwan.modules.account.dto.SignUpFormDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class RestAccountController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value={"/sign-in", "/login"})
    public ResponseEntity<?> login(@ModelAttribute LoginDto loginDto) {
        log.info("requestDto : {}", loginDto.toString());
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.usernameOrEmail(), loginDto.password(), List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticate);
        return ResponseEntity.ok("ok");
    }


    @PostMapping(value = {"/register", "/sign-up"})
    public ResponseEntity<?> register(@ModelAttribute SignUpFormDto requestDto) throws Exception {
        log.info("request id : {}", requestDto.toString());
        Account account = accountService.processNewAccount(requestDto);
        return ResponseEntity.ok(account);
    }
}
