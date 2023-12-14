package be.shwan.modules.main.rest;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class RestSearchController {
    private final StudyRepository studyRepository;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity getStudyList(@CurrentUser Account account, @RequestParam String keyword,
                                  @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        Page<Study> studyList = studyRepository.findByKeyword(keyword, pageable);
        result.put("account", account == null ? SecurityContextHolder.getContext().getAuthentication().getPrincipal() : account);
        result.put("studyPage", studyList);
        result.put("keyword", keyword);
        result.put("sortProperty", getDefaultSortCondition(pageable));
        return ResponseEntity.ok(objectMapper.writeValueAsString(result));
    }

    private String getDefaultSortCondition(Pageable pageable) {
        String property = pageable.getSort().toList().get(0).getProperty();
        return property.isEmpty() ? "publishedDateTime": property;
    }
}
