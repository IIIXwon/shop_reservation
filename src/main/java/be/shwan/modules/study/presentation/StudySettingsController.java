package be.shwan.modules.study.presentation;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingsController {

    private final StudyService studyService;
    private final ZoneService zoneService;
    private final TagRepository tagRepository;

    private final ObjectMapper objectMapper;
    private final StudyPathRequestDtoValidator studyPathRequestDtoValidator;
    static final String STUDY_SETTINGS_VIEW = "study/settings/study";
    static final String STUDY_SETTING_DESCRIPTION_VIEW = "study/settings/description";
    static final String STUDY_SETTING_BANNER_VIEW = "study/settings/banner";
    static final String STUDY_SETTING_ZONE_VIEW = "study/settings/zones";

    @InitBinder("studyPathRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyPathRequestDtoValidator);
    }
    @GetMapping(value = {"/description"})
    public String studyDescriptionFormPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study byPath = studyService.getStudyToUpdate(path, account);
        model.addAttribute(byPath);
        model.addAttribute(new StudyDescriptionRequestDto(byPath.getShortDescription(), byPath.getFullDescription()));
        return STUDY_SETTING_DESCRIPTION_VIEW;
    }

    @PostMapping(value = {"/description"})
    public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path,
                                         @Valid StudyDescriptionRequestDto studyDescriptionRequestDto, Errors errors,
                                         RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return STUDY_SETTING_DESCRIPTION_VIEW;
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
        return STUDY_SETTING_BANNER_VIEW;
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
        return STUDY_SETTING_ZONE_VIEW;
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

    @GetMapping(value = {"/study"})
    public String studyFormPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getSimpleStudy(path, account);
        model.addAttribute(study);
        model.addAttribute(account);
        return STUDY_SETTINGS_VIEW;
    }

    @PostMapping(value = {"/study/publish"})
    public String studyPublish(@CurrentUser Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.publish(study);
        redirectAttributes.addFlashAttribute("message", "스터디가 공개 되었습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/study";
    }

    @PostMapping(value = {"/study/close"})
    public String studyClose(@CurrentUser Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.closed(study);
        redirectAttributes.addFlashAttribute("message", "스터디가 종료 되었습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/study";
    }

    @PostMapping(value = {"/recruit/start"})
    public String startRecruiting(@CurrentUser Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.startRecruit(study);
        redirectAttributes.addFlashAttribute("message", "스터디 팀원 모집을 시작합니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/study";
    }

    @PostMapping(value = {"/recruit/stop"})
    public String stopRecruiting(@CurrentUser Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getSimpleStudy(path, account);
        studyService.stopRecruit(study);
        redirectAttributes.addFlashAttribute("message", "스터디 팀원 모집을 종료합니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/study";
    }

    @PostMapping(value = {"/study/path"})
    public String updateStudyPath(@CurrentUser Account account, @PathVariable String path, @Valid StudyPathRequestDto studyPathRequestDto,
                                  Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return STUDY_SETTINGS_VIEW;
        }
        Study study = studyService.getSimpleStudy(path, account);
        studyService.updateStudyPath(study, studyPathRequestDto);
        redirectAttributes.addFlashAttribute("message", "스터디 경로를 변경했습니다.");
        return "redirect:/study/" + encodePath(study.getPath()) + "/settings/study";
    }

    @PostMapping(value = {"/study/title"})
    public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path, @Valid StudyTitleRequestDto studyTitleRequestDto,
                                   Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return STUDY_SETTINGS_VIEW;
        }
        Study study = studyService.getSimpleStudy(path, account);
        studyService.updateStudyTitle(study, studyTitleRequestDto);
        redirectAttributes.addFlashAttribute("message", "스터디 제목을 변경했습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/study";
    }

    @PostMapping(value = {"/study/remove"})
    public String removeStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithMembersAndManagers(path);
        studyService.removeStudy(study, account);
        return "redirect:/";
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
