package be.shwan.modules.account;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.dto.SignUpFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String[] nicknames = withAccount.value();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        for(String nickname : nicknames) {
            SignUpFormDto signUpForm = new SignUpFormDto(nickname, nickname +"@email.com", "12345678");
            try {
                accountService.processNewAccount(signUpForm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            UserDetails principal = accountService.loadUserByUsername(nickname);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
            context.setAuthentication(authentication);
        }
        return context;
    }
}
