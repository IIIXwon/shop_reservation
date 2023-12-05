package be.shwan.settings;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.settings.dto.ProfileInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = {"/settings"})
@RequiredArgsConstructor
public class SettingsController {

    private final String PROFILE_VIEW = "/settings/profile";
    private final AccountService accountService;
    @GetMapping(value = {"/profile"})
    public String profilePage(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ProfileInfo(account.getBio(), account.getUrl(), account.getOccupation(),
                account.getLocation(), account.getProfileImage()));
        return PROFILE_VIEW;
    }

    @PostMapping(value = {"/profile"})
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
}
