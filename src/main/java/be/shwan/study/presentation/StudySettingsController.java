package be.shwan.study.presentation;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.dto.StudyDescriptionRequestDto;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingsController {

    private final StudyService studyService;
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

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
