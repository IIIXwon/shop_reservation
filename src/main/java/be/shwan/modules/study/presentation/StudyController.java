package be.shwan.modules.study.presentation;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.dto.StudyRequestDto;
import be.shwan.modules.study.dto.StudyRequestDtoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;


    private final StudyRequestDtoValidator studyRequestDtoValidator;
    static final String STUDY_FORM_VIEW = "study/form";
    static final String STUDY_VIEW_PATH = "study/view";
    static final String STUDY_MEMBER_VIEW = "study/members";

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
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }



    @GetMapping(value = {"/study/{path}"})
    public String studyViewPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study byPath =  studyService.getStudy(path, account);
        model.addAttribute(byPath);
        model.addAttribute(account);
        return STUDY_VIEW_PATH;
    }

    @GetMapping(value = {"/study/{path}/members"})
    public String studyMemberPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study byPath = studyService.getStudy(path, account);
        model.addAttribute(byPath);
        return STUDY_MEMBER_VIEW;
    }

    @PostMapping(value = {"/study/{path}/join"})
    public String joinStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        studyService.join(study, account);
        return "redirect:/study/" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @PostMapping(value = {"/study/{path}/leave"})
    public String leaveStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        studyService.leave(study, account);
        return "redirect:/study/" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @Deprecated
    @GetMapping(value = {"/study/data"})
    public String testData(@CurrentUser Account account) {
        studyService.generateTestdatas(account);
        return "redirect:/";
    }
}
