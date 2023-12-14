package be.shwan.modules.main.rest;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.AccountRepository;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.event.domain.EnrollmentRepository;
import be.shwan.modules.study.domain.StudyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestMainController {
    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;

    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity home(@CurrentUser Account account) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        if (account != null) {
            Account byId = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            result.put("account", byId);
            result.put("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrollAtDesc(byId, true));
            result.put("studyList",
                    studyRepository.findStudyListWithTagsAndZonesByAccount(byId.getTags(), byId.getZones()));
            result.put("studyManagerOf",
                    studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(byId, false));
            result.put("studyMemberOf",
                    studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(byId, false));
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
        }
        result.put("studyList", studyRepository.findDefault());
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }
}
