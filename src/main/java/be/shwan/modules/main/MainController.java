package be.shwan.modules.main;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final StudyRepository studyRepository;
    static final String MAIN_VEIW = "index";

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        List<Study> studyList = studyRepository.findDefault();
        model.addAttribute("studyList", studyList);
        return MAIN_VEIW;
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "login";
    }
}
