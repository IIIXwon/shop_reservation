package be.shwan.study.presentation;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.account.domain.UserAccount;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
import be.shwan.study.domain.StudyRepository;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.study.dto.StudyRequestDtoValidator;
import be.shwan.study.dto.StudyRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    private final String STUDY_FORM_VIEW = "study/form";

    private final StudyRequestDtoValidator studyRequestDtoValidator;
    private final String STUDY_DESCRIPTION_SETTING_VIEW = "study/settings/description";

    @InitBinder("studyRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyRequestDtoValidator);
    }

    @GetMapping(value = {"/new-study"})
    public String studyFormPage(Model model) {
        model.addAttribute(new StudyRequestDto("", "", "", ""));
        return STUDY_FORM_VIEW;
    }

    @PostMapping(value = {"/new-study"})
    public String newStudy(@CurrentUser Account account, @Valid StudyRequestDto studyRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return STUDY_FORM_VIEW;
        }
        Study study = studyService.newStudy(account, studyRequestDto);
        return "redirect:/study/" + encodePath(study);
    }



    @GetMapping(value = {"/study/{path}"})
    public String studyViewPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study byPath =  studyService.getStudy(path);
        model.addAttribute(byPath);
        model.addAttribute(account);
        return "study/view";
    }

    @GetMapping(value = {"/study/{path}/members"})
    public String studyMemberPage(@PathVariable String path, Model model) {
        Study byPath = studyService.getStudy(path);
        model.addAttribute(byPath);
        return "study/members";
    }

    @GetMapping(value = {"/study/{path}/settings/description"})
    public String studyDescriptionFormPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study byPath = studyService.getStudyToUpdate(path, account);
        model.addAttribute(byPath);
        model.addAttribute(new StudyDescriptionRequestDto(byPath.getShortDescription(), byPath.getFullDescription()));
        return STUDY_DESCRIPTION_SETTING_VIEW;
    }

    @PostMapping(value = {"/study/{path}/settings/description"})
    public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path, Model model,
                                         @Valid StudyDescriptionRequestDto studyDescriptionRequestDto, Errors errors,
                                         RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return STUDY_DESCRIPTION_SETTING_VIEW;
        }
        Study byPath = studyService.getStudyToUpdate(path, account);
        studyService.updateDescription(byPath, studyDescriptionRequestDto);
        redirectAttributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/study/" + encodePath(byPath) + "/settings/description";
    }

    private String encodePath(Study study) {
        return URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }
}
