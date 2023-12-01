package be.shwan.account.application.impl;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleAccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    private Account signUp(SignUpFormDto requestDto) {
        Account account = new Account(requestDto.nickname(), passwordEncoder.encode(requestDto.password()),
                requestDto.email());
        return accountRepository.save(account);
    }

    @Override
    public AccountResponseRecord getAccountInfo(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        return getAccountRecord(account);
    }

    @Override
    public Account processNewAccount(SignUpFormDto signUpFormDto) {
        Account account = signUp(signUpFormDto);
        account.generateEmailCheckToken();
        sendEmail(account);
        return account;
    }

    @Override
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                account.getNickname(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private void sendEmail(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("스터디올레 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        mailMessage.setTo(account.getEmail());
        javaMailSender.send(mailMessage);
    }


    private AccountResponseRecord getAccountRecord(Account newAccount) {
        return AccountResponseRecord.builder()
                .id(newAccount.getId())
                .nickname(newAccount.getNickname())
                .email(newAccount.getEmail())
                .active(newAccount.isActive())
                .build();
    }
}
