package be.shwan.modules.account.presentation;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.account.dto.SignUpFormDto;
import be.shwan.modules.account.dto.SignUpFormValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    private final SignUpFormValidator signUpFormValidator;

    static final String SIGN_UP_PAGE = "accounts/sign-up";
    static final String CHECK_EMAIL_VIEW = "accounts/check-email";
    static final String REDIRECT_ROOT = "redirect:/";
    static final String CHECK_LOGIN_EMAIL = "accounts/check-login-email";
    static final String LOGGED_IN_BY_EMAIL = "accounts/logged-in-by-email";
    static final String CHECKED_EMAIL_VIEW = "accounts/checked-email";
    static final String EMAIL_LOGIN_VIEW = "accounts/email-login";

    @InitBinder("signUpFormDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping(value = {"/sign-up"})
    public String signUpPage(Model model) {
        SignUpFormDto signUpFormDto = new SignUpFormDto("", "", "");
        model.addAttribute(signUpFormDto);
        return SIGN_UP_PAGE;
    }

    @PostMapping(value = {"/sign-up"})
    public String signUpSubmit(@Valid @ModelAttribute SignUpFormDto signUpFormDto, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return SIGN_UP_PAGE;
        }

        Account account = accountService.processNewAccount(signUpFormDto);
        accountService.login(account);
        SecurityContextHolder.getContext();
        return REDIRECT_ROOT;
    }

    @GetMapping(value = {"/check-email-token"})
    public String checkEmailToken(@RequestParam String token, @RequestParam String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return CHECKED_EMAIL_VIEW;
        }

        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return CHECKED_EMAIL_VIEW;
        }


        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return CHECKED_EMAIL_VIEW;
    }

    @GetMapping(value = {"/check-email"})
    public String checkEmailPage(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return CHECK_EMAIL_VIEW;
    }

    @GetMapping(value = {"/resend-check-email-token"})
    public String resendCheckEmailToken(@CurrentUser Account account, Model model) {
        if (!account.isValidIssueTokenTime()) {
            model.addAttribute("email", account.getEmail());
            model.addAttribute("error", "이메일 재전송을 1시간에 1회 가능 합니다");
            return CHECK_EMAIL_VIEW;
        }
        accountService.sendEmailToken(account);
        return REDIRECT_ROOT;
    }

    @GetMapping(value = {"/profile/{nickname}"})
    public String profilePage(@PathVariable String nickname, @CurrentUser Account account, Model model) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        return "accounts/profile";
    }

    @GetMapping(value = {"/email-login"})
    public String emailLoginPage() {
        return EMAIL_LOGIN_VIEW;
    }

    @PostMapping(value = {"/email-login"})
    public String emailLoginPage(@RequestParam String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "");
            model.addAttribute("email", email);
            return CHECK_LOGIN_EMAIL;
        }

        if (account.getEmailLoginTokenIssueTime() != null && !account.isValidEmailLoginToken()) {
            model.addAttribute("error", "이메일 로그인은 1시간 뒤에 사용할 수 있습니다.");
            return CHECK_LOGIN_EMAIL;
        }
        accountService.sendEmailLoginUrl(account);
        return CHECK_LOGIN_EMAIL;
    }

    @GetMapping(value = {"/login-by-email"})
    public String emailLoginCompletePage(@RequestParam String token, @RequestParam String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "");
            return LOGGED_IN_BY_EMAIL;
        }
        accountService.sendEmailLogin(account, token);
        return LOGGED_IN_BY_EMAIL;
    }
}
