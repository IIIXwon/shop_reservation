package be.shwan.modules.account;

import be.shwan.modules.account.application.AccountService;
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
    private final AccountFactory accountFactory;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String[] nicknames = withAccount.value();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        for(String nickname : nicknames) {
            try {
                accountFactory.createAccount(nickname);
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
