package be.shwan.modules.study.presentation.rest;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyDescriptionRequestDto;
import be.shwan.modules.study.dto.StudyPathRequestDto;
import be.shwan.modules.study.dto.StudyPathRequestDtoValidator;
import be.shwan.modules.study.dto.StudyTitleRequestDto;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.domain.TagRepository;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.dto.RequestZoneDto;
import be.shwan.modules.zone.dto.ResponseZoneDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/study/{path}/settings")
public class RestStudySettingsController {

    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final ZoneService zoneService;
    private final TagRepository tagRepository;

    private final ObjectMapper objectMapper;
    private final StudyPathRequestDtoValidator studyPathRequestDtoValidator;

    @InitBinder("studyPathRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyPathRequestDtoValidator);
    }

    @GetMapping(value = {"/info"})
    public ResponseEntity getStudy(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study byPath = studyService.getStudyToUpdate(path, account);
        Study study = studyRepository.findStudyWithTagsAndZonesById(byPath.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("study", study);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/description"})
    public ResponseEntity updateStudyDescription(@CurrentUser Account account, @PathVariable String path,
                                                 @RequestBody @Valid StudyDescriptionRequestDto studyDescriptionRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Study byPath = studyService.getStudyToUpdate(path, account);
        studyService.updateDescription(byPath, studyDescriptionRequestDto);
        return ResponseEntity.ok().body("스터디 소개를 수정했습니다.");
    }

    @PostMapping(value = {"/banner"})
    public ResponseEntity studyBannerImageUpdate(@CurrentUser Account account, @PathVariable String path,
                                                 @RequestBody Map<String, String> image) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateBannerImage(study, image.get("image"));
        return ResponseEntity.ok().body("스터디 이미지를 수정했습니다.");
    }

    @PostMapping(value = {"/banner/enable"})
    public ResponseEntity enableBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.enableBanner(study);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/banner/disable"})
    public ResponseEntity disableBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.disableBanner(study);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/tags"})
    public ResponseEntity tagPage(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        studyService.getStudyToUpdate(path, account);
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).toList();
        Map<String, Object> result = new HashMap<>();
        result.put("whitelist", allTags);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/tags/add"})
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody RequestTagDto tagDto) {
        Study study = null;
        try {
            study = studyService.getStudyToUpdateTag(path, account);
        } catch (Exception e) {
            log.error("study tag add error : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        studyService.addTag(study, tagDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/tags/remove"})
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody RequestTagDto tagDto) {
        Study study = null;
        try {
            study = studyService.getStudyToUpdateTag(path, account);
        } catch (Exception e) {
            log.error("study tag remove error : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTag(study, tagDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/zones"})
    public ResponseEntity zonePage(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        studyService.getStudyToUpdate(path, account);
        List<String> zoneStr = zoneService.tagifyZonesToString();
        Map<String, Object> result = new HashMap<>();
        result.put("whitelist", zoneStr);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/zones/add"})
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path, @RequestBody RequestZoneDto zoneDto) {
        Study study = null;
        try {
            study = studyService.getStudyToUpdateZone(path, account);
        } catch (Exception e) {
            log.error("study tag add error : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        studyService.addZone(study, zoneDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/zones/remove"})
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path, @RequestBody RequestZoneDto zoneDto) {
        Study study = null;
        try {
            study = studyService.getStudyToUpdate(path, account);
        } catch (Exception e) {
            log.error("study tag remove error : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        studyService.removeZone(study, zoneDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/publish"})
    public ResponseEntity studyPublish(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.publish(study);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "스터디가 공개 되었습니다.");
        String redirectUrl = "redirect:/study/" + study.getEncodePath() + "/settings/study";
        result.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/close"})
    public ResponseEntity studyClose(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.closed(study);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "스터디가 종료 되었습니다.");
        String redirectUrl = "redirect:/study/" + study.getEncodePath() + "/settings/study";
        result.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/recruit/start"})
    public ResponseEntity startRecruiting(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.startRecruit(study);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "스터디 팀원 모집을 시작합니다.");
        String redirectUrl = "redirect:/study/" + study.getEncodePath() + "/settings/study";
        result.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/recruit/stop"})
    public ResponseEntity stopRecruiting(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.stopRecruit(study);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "스터디 팀원 모집을 종료합니다.");
        String redirectUrl = "redirect:/study/" + study.getEncodePath() + "/settings/study";
        result.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/path"})
    public ResponseEntity updateStudyPath(@CurrentUser Account account, @PathVariable String path, @RequestBody @Valid StudyPathRequestDto studyPathRequestDto,
                                          Errors errors) throws JsonProcessingException {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Study study = studyService.getSimpleStudy(path, account);
        studyService.updateStudyPath(study, studyPathRequestDto);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "스터디 경로를 변경했습니다.");
        String redirectUrl = "redirect:/study/" + study.getEncodePath() + "/settings/study";
        result.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/title"})
    public ResponseEntity updateStudyTitle(@CurrentUser Account account, @PathVariable String path, @RequestBody @Valid StudyTitleRequestDto studyTitleRequestDto,
                                           Errors errors) throws JsonProcessingException {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Study study = studyService.getSimpleStudy(path, account);
        studyService.updateStudyTitle(study, studyTitleRequestDto);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "스터디 제목을 변경했습니다.");
        String redirectUrl = "redirect:/study/" + study.getEncodePath() + "/settings/study";
        result.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @DeleteMapping
    public ResponseEntity removeStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithMembersAndManagers(path);
        studyService.removeStudy(study, account);
        return ResponseEntity.ok().build();
    }
}
