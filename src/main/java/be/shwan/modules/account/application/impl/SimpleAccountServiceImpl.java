package be.shwan.modules.account.application.impl;

import be.shwan.infra.config.AppProperties;
import be.shwan.infra.mail.application.EmailService;
import be.shwan.infra.mail.dto.EmailMessage;
import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.domain.UserAccount;
import be.shwan.modules.account.dto.*;
import be.shwan.modules.account.event.EmailCreateEvent;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.zone.domain.Zone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SimpleAccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;

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
        sendEmailToken(account);
        return account;
    }

    @Override
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public void sendEmailLogin(Account account, String token) {
        if (account.isValidEmailLoginToken(token)) {
            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                    new UserAccount(account),
                    account.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(user);
        } else {
            throw new IllegalArgumentException("유효하지 않은 " + token + " 값 입니다.");
        }
    }

    @Override
    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.getTags().add(tag);
        });
    }

    @Override
    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    @Override
    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.removeTag(tag));
    }

    @Override
    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.addZone(zone);
        });
    }

    @Override
    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.removeZone(zone));
    }

    @Override
    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void sendEmailToken(Account account) {
        String linkMessage = "/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail();
        String process = htmlEmailTemplate(account, linkMessage);
        EmailMessage emailMessage = new EmailMessage(account.getEmail(), "스터디올레 회원 가입 인증",
                process);
        account.sendEmailCheckToken();
        eventPublisher.publishEvent(new EmailCreateEvent(emailMessage));
//        emailService.sendEmail(emailMessage);
    }

    @Override
    public void sendEmailLoginUrl(Account account) {
        account.issueEmailLoginToken();
        String linkMessage = "/login-by-email?token=" + account.getEmailLoginToken() + "&email=" + account.getEmail();
        String htmlEmailTemplate = htmlEmailTemplate(account, linkMessage);
        EmailMessage emailMessage = new EmailMessage(account.getEmail(), "스터디올레 이메일 로그인 안내", htmlEmailTemplate);
        eventPublisher.publishEvent(new EmailCreateEvent(emailMessage));
//        emailService.sendEmail(emailMessage);
    }

    private String htmlEmailTemplate(Account account, String linkMessage) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", linkMessage);
        String process = templateEngine.process("mail/simple-link", context);
        return process;
    }


    @Override
    public void completeSignUp(Account account) {
        account.verify();
        login(account);
    }

    @Override
    public void updateProfile(Account account, ProfileInfo profileInfo) {
        account.updateProfile(profileInfo);
        accountRepository.save(account);
    }

    @Override
    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.updatePassword(passwordEncoder.encode(passwordForm.newPassword()));
        accountRepository.save(account);
    }

    @Override
    public void updateNotification(Account account, Notifications notifications) {
        account.updateNotification(notifications);
        accountRepository.save(account);
    }

    @Override
    public void updateAccount(Account account, NicknameForm signUpFormDto) {
        account.updateAccount(signUpFormDto);
        login(account);
    }




    private AccountResponseRecord getAccountRecord(Account newAccount) {
        return AccountResponseRecord.builder()
                .id(newAccount.getId())
                .nickname(newAccount.getNickname())
                .email(newAccount.getEmail())
                .active(newAccount.isActive())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String nicknameOrEmail) throws UsernameNotFoundException {
        Account account = accountRepository.findByNickname(nicknameOrEmail);
        if (account == null ) {
            account = accountRepository.findByEmail(nicknameOrEmail);
        }

        if (account == null ) {
            throw new UsernameNotFoundException(nicknameOrEmail);
        }

        return new UserAccount(account);
    }
}
