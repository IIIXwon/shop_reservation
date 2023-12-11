package be.shwan.modules.main;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.event.domain.EnrollmentRepository;
import be.shwan.modules.study.domain.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;
    static final String MAIN_VIEW = "index";
    static final String LOGIN_MAIN_VIEW = "index-after-login";

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            Account byId = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute("account", byId);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrollAtDesc(byId, true));
            model.addAttribute("studyList",
                    studyRepository.findStudyListWithTagsAndZonesByAccount(byId.getTags(), byId.getZones()));
            model.addAttribute("studyManagerOf",
                    studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(byId, false));
            model.addAttribute("studyMemberOf",
                    studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(byId, false));
            return LOGIN_MAIN_VIEW;
        }
        model.addAttribute("studyList", studyRepository.findDefault());
        return MAIN_VIEW;
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "login";
    }
}
