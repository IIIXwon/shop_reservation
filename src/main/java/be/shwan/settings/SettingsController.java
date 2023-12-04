package be.shwan.settings;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.settings.dto.ProfileInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/settings"})
public class SettingsController {
    @GetMapping(value = {"/profile"})
    public String profilePage(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ProfileInfo(account.getBio(), account.getUrl(), account.getOccupation(),
                account.getLocation()));
        return "/settings/profile";
    }
}
