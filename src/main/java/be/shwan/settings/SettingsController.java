package be.shwan.settings;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.settings.dto.Notifications;
import be.shwan.settings.dto.PasswordForm;
import be.shwan.settings.dto.PasswordFormValidator;
import be.shwan.settings.dto.ProfileInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
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
    private final String NOTIFICATION_PATH = "/notifications";
    private final String NOTIFACATION_VIEW = "/settings/notifications";

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

    @GetMapping(NOTIFICATION_PATH)
    public String notificationPage(@CurrentUser Account account, Model model) {
        model.addAttribute(new Notifications(account.isStudyCreatedByEmail(), account.isStudyCreatedByWeb(), account.isStudyEnrollmentResultByEmail(),
                account.isStudyEnrollmentResultByWeb(), account.isStudyUpdatedByEmail(), account.isStudyUpdatedByWeb()));
        return NOTIFACATION_VIEW;
    }

    @PostMapping(NOTIFICATION_PATH)
    public String updateNotification(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
                                     RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return NOTIFACATION_VIEW;
        }
        accountService.updateNotification(account, notifications);
        redirectAttributes.addFlashAttribute("message", "알림을 수정 했습니다.");
        return "redirect:/settings/notifications";
    }
}
