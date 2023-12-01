package be.shwan.account.presentation;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.account.dto.SignUpFormValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    private final SignUpFormValidator signUpFormValidator;
    private final JavaMailSender javaMailSender;
    private final String SIGN_UP_PAGE = "accounts/sign-up";

    @InitBinder("signUpFormDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping(value = {"/sign-up"})
    public String signUpPage(Model model) {
        SignUpFormDto signUpFormDto = SignUpFormDto.builder()
                .email("")
                .password("")
                .nickname("")
                .build();
        model.addAttribute(signUpFormDto);
        return SIGN_UP_PAGE;
    }

    @PostMapping(value = {"/sign-up"})
    public String signUpSubmit(@Valid @ModelAttribute SignUpFormDto signUpFormDto, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return SIGN_UP_PAGE;
        }

        Account account = accountService.signUp(signUpFormDto);
        account.generateEmailCheckToken();
        sendEmail(account);

        return "redirect:/";
    }

    private void sendEmail(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("스터디올레 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        mailMessage.setTo(account.getEmail());
        javaMailSender.send(mailMessage);
    }
}
