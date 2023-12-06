package be.shwan.study.presentation;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.tag.application.TagService;
import be.shwan.tag.domain.Tag;
import be.shwan.tag.domain.TagRepository;
import be.shwan.tag.dto.RequestTagDto;
import be.shwan.zone.application.ZoneService;
import be.shwan.zone.dto.RequestZoneDto;
import be.shwan.zone.dto.ResponseZoneDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingsController {

    private final AccountService accountService;
    private final StudyService studyService;
    private final ZoneService zoneService;
    private final TagRepository tagRepository;

    private final ObjectMapper objectMapper;
    private final String STUDY_DESCRIPTION_SETTING_VIEW = "study/settings/description";

    @GetMapping(value = {"/description"})
    public String studyDescriptionFormPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study byPath = studyService.getStudyToUpdate(path, account);
        model.addAttribute(byPath);
        model.addAttribute(new StudyDescriptionRequestDto(byPath.getShortDescription(), byPath.getFullDescription()));
        return STUDY_DESCRIPTION_SETTING_VIEW;
    }

    @PostMapping(value = {"/description"})
    public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path,
                                         @Valid StudyDescriptionRequestDto studyDescriptionRequestDto, Errors errors,
                                         RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return STUDY_DESCRIPTION_SETTING_VIEW;
        }
        Study byPath = studyService.getStudyToUpdate(path, account);
        studyService.updateDescription(byPath, studyDescriptionRequestDto);
        redirectAttributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/description";
    }

    @GetMapping(value = {"/banner"})
    public String studyBannerPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        model.addAttribute(study);
        return "study/settings/banner";
    }

    @PostMapping(value = {"/banner"})
    public String studyBannerImageUpdate(@CurrentUser Account account, @PathVariable String path,
                                         @RequestParam String image, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateBannerImage(study, image);
        redirectAttributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/banner";
    }

    @PostMapping(value = {"/banner/enable"})
    public String enableBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.enableBanner(study);
        return "redirect:/study/" + encodePath(path) + "/settings/banner";
    }

    @PostMapping(value = {"/banner/disable"})
    public String disableBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.disableBanner(study);
        return "redirect:/study/" + encodePath(path) + "/settings/banner";
    }

    @GetMapping(value = {"/tags"})
    public String tagPage(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(path, account);
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).toList();
        String attributeValue = objectMapper.writeValueAsString(allTags);
        model.addAttribute("whitelist", attributeValue);
        List<String> collect = study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("tags", collect);
        model.addAttribute(study);
        return "study/settings/tags";
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
    public String zonePage(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(path, account);
        List<ResponseZoneDto> zones = zoneService.tagifyZonesToResponseZoneDto(study.getZones());
        List<String> zoneStr = zoneService.tagifyZonesToString();
        model.addAttribute("zones", zones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zoneStr));
        model.addAttribute(study);
        return "study/settings/zones";
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

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
