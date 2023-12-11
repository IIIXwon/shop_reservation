package be.shwan.modules.study.presentation;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final StudyRepository studyRepository;
    static final String STUDY_SEARCH_VIEW = "search";

    @GetMapping("/search/study")
    public String studySearchPage(@CurrentUser Account account, @RequestParam String keyword, Model model,
          @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Study> studyList = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("account", account == null ? SecurityContextHolder.getContext().getAuthentication().getPrincipal() : account);
        model.addAttribute("studyPage", studyList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", getDefaultSortCondition(pageable));
        return STUDY_SEARCH_VIEW;
    }

    private String getDefaultSortCondition(Pageable pageable) {
        String property = pageable.getSort().toList().get(0).getProperty();
        return property.isEmpty() ? "publishedDateTime": property;
    }
}
