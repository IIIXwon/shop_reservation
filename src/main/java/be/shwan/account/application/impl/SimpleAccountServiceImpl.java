package be.shwan.account.application.impl;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.AccountResponseRecord;
import be.shwan.account.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleAccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Account signUp(SignUpRequestDto requestDto) throws Exception {
        Account account = new Account(requestDto.nickname(), passwordEncoder.encode(requestDto.password()),
                requestDto.email());
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    @Override
    public AccountResponseRecord getAccountInfo(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        return getAccountRecord(account);
    }



    private AccountResponseRecord getAccountRecord(Account newAccount) {
        return AccountResponseRecord.builder()
                .id(newAccount.getId())
                .nickname(newAccount.getNickname())
                .email(newAccount.getEmail().toString())
                .active(newAccount.isActive())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByNickname(username);
        return new User(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
