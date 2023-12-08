package be.shwan.modules.main;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "login";
    }
}
