package be.shwan.account.application.impl;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.domain.UserAccount;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.settings.dto.NicknameForm;
import be.shwan.settings.dto.Notifications;
import be.shwan.settings.dto.PasswordForm;
import be.shwan.settings.dto.ProfileInfo;
import be.shwan.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public void sendEmailToken(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("스터디올레 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        mailMessage.setTo(account.getEmail());
        javaMailSender.send(mailMessage);
        account.sendEmailCheckToken();
    }

    @Override
    public void sendEmailLoginUrl(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        account.issueEmailLoginToken();
        mailMessage.setSubject("스터디올레 이메일 로그인 안내");
        mailMessage.setText("/login-by-email?token=" + account.getEmailLoginToken() + "&email=" + account.getEmail());
        mailMessage.setTo(account.getEmail());
        javaMailSender.send(mailMessage);
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
