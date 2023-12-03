package be.shwan.account.presentation;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.domain.CurrentUser;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.account.dto.SignUpFormValidator;
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

    private final String SIGN_UP_PAGE = "accounts/sign-up";
    private final String CHECK_EMAIL_VIEW = "accounts/check-email";
    private final String REDIRECT_ROOT = "redirect:/";

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

        Account account = accountService.processNewAccount(signUpFormDto);
        accountService.login(account);
        SecurityContextHolder.getContext();
        return REDIRECT_ROOT;
    }

    @GetMapping(value = {"/check-email-token"})
    public String checkEmailToken(@RequestParam String token, @RequestParam String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "accounts/checked-email";
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }


        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
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
        if(byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        return "accounts/profile";
    }
}
