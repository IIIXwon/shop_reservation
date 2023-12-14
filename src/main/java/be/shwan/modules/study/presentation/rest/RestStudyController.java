package be.shwan.modules.study.presentation.rest;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.dto.StudyRequestDto;
import be.shwan.modules.study.dto.StudyRequestDtoValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class RestStudyController {
    private final StudyService studyService;
    private final ObjectMapper objectMapper;

    private final StudyRequestDtoValidator studyRequestDtoValidator;

    @InitBinder("studyRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyRequestDtoValidator);
    }

    @PostMapping
    public ResponseEntity newStudy(@CurrentUser Account account, @RequestBody @Valid StudyRequestDto studyRequestDto, Errors errors) throws URISyntaxException {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Study study = studyService.newStudy(account, studyRequestDto);
        String redirect = "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
        URI uri = new URI(redirect);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = {"/{path}"})
    public ResponseEntity getStudy(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study byPath =  studyService.getStudy(path, account);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(byPath));
    }

    @GetMapping(value = {"/{path}/members"})
    public ResponseEntity studyMemberPage(@CurrentUser Account account,  @PathVariable String path) throws JsonProcessingException {
        Study byPath = studyService.getStudy(path, account);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(byPath.getMembers()));
    }

    @PostMapping(value = {"/{path}/join"})
    public ResponseEntity joinStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        studyService.join(study, account);
        return ResponseEntity.ok().body("'" + study.getTitle() + "' 스터디에 " + account.getNickname() +  " 님이 참가 했습니다.");
    }

    @PostMapping(value = {"/{path}/leave"})
    public ResponseEntity leaveStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        studyService.leave(study, account);
        return ResponseEntity.ok().body("'" + study.getTitle() + "' 스터디에서 " + account.getNickname() +  " 님이 탈퇴 했습니다.");
    }
}
