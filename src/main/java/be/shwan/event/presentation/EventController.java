package be.shwan.event.presentation;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.CurrentUser;
import be.shwan.event.application.EventService;
import be.shwan.event.domain.Event;
import be.shwan.event.domain.EventRepository;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
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
        Event event = eventService.createEvent(account, study, eventRequestDto);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping(value = {"/study/{path}/events/{id}"})
    public String eventViewPage(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                 Model model) {
        Study study = studyService.getStudyWithMembersAndManagers(path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        return "events/view";
    }

    @GetMapping(value = {"/study/{path}/events"})
    String eventViewList(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);

        List<Event> events = eventService.getEventByStudy(study);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        for(Event event : events) {
            if (event.isEndEvent()) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        }

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);
        return "study/events";
    }


    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }
}