package be.shwan.event.presentation;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.event.application.EventService;
import be.shwan.event.domain.EventType;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.event.dto.EventRequestDtoValidator;
import be.shwan.study.application.StudyService;
import be.shwan.study.domain.Study;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final StudyService studyService;

    private final EventRequestDtoValidator eventRequestDtoValidator;

    @InitBinder("eventRequestDto")
    public void setEventRequestDtoValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventRequestDtoValidator);
    }
    final String EVENT_FORM_VIEW = "events/form";

    @GetMapping(value = {"/study/{path}/new-event"})
    public String eventFormPage(@CurrentUser Account account, @PathVariable String path, Model model) {
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter();
        Study study = studyService.getStudyWithMembersAndManagers(path);
        model.addAttribute(new EventRequestDto("", EventType.FCFS, 0, LocalDateTime.now().format(dateTimeFormatter),
                LocalDateTime.now().format(dateTimeFormatter), LocalDateTime.now().format(dateTimeFormatter),  ""));
        model.addAttribute(study);
        model.addAttribute(account);
        return EVENT_FORM_VIEW;
    }

    @PostMapping(value = {"/study/{path}/new-event"})
    public String newEvent(@CurrentUser Account account, @PathVariable String path, @Valid EventRequestDto eventRequestDto,
                           Errors errors, Model model) {
        Study study = studyService.getStudyWithMembersAndManagers(path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return EVENT_FORM_VIEW;
        }
        eventService.createEvent(account, study, eventRequestDto);
        return "redirect:/study/" + study.getPath();
    }


    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }
}