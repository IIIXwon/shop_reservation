package be.shwan.settings;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.settings.dto.PasswordForm;
import be.shwan.settings.dto.PasswordFormValidator;
import be.shwan.settings.dto.ProfileInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = {"/settings"})
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    final String PROFILE_PATH = "/profile";
    final String PROFILE_VIEW = "/settings/profile";
    final String PASSWORD_VIEW = "/settings/password";
    private final String PASSWORD_PATH = "/password";

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }
    @GetMapping(value = {PROFILE_PATH})
    public String profilePage(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ProfileInfo(account.getBio(), account.getUrl(), account.getOccupation(),
                account.getLocation(), account.getProfileImage()));
        return PROFILE_VIEW;
    }

    @PostMapping(value = {PROFILE_PATH})
    public String updateProfile(@CurrentUser Account account, @Valid ProfileInfo profileInfo, Errors errors,
                                Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return PROFILE_VIEW;
        }
        accountService.updateProfile(account, profileInfo);
        redirectAttributes.addFlashAttribute("message", "프로필을 수정 했습니다.");
        return "redirect:/settings/profile";
    }

    @GetMapping(value = {PASSWORD_PATH})
    public String passwordPage(Model model){
        model.addAttribute(new PasswordForm("", ""));
        return PASSWORD_VIEW;
    }

    @PostMapping(value = {PASSWORD_PATH})
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return PASSWORD_VIEW;
        }
        accountService.updatePassword(account, passwordForm);
        redirectAttributes.addFlashAttribute("message", "비밀번호를 수정 했습니다.");
        return "redirect:/settings/password";
    }
}
