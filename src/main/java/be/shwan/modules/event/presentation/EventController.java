package be.shwan.modules.event.presentation;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.*;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.event.dto.EventRequestDtoValidator;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final StudyService studyService;
    private final EnrollmentRepository enrollmentRepository;


    private final EventRequestDtoValidator eventRequestDtoValidator;
    static final String EVENT_FORM_VIEW = "events/form";
    static final String EVENT_UPDATE_FORM_VIEW = "events/update-form";
    static final String EVENT_VIEW = "events/view";
    static final String STUDY_EVENT_VIEW = "study/events";

    @InitBinder("eventRequestDto")
    public void setEventRequestDtoValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventRequestDtoValidator);
    }

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
        return EVENT_VIEW;
    }

    @GetMapping(value = {"/study/{path}/events"})
    String eventViewList(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);

        List<Event> events = eventService.getEventByStudy(study);
        List<List<Event>> eventList = eventService.getEventList(events);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("newEvents", eventList.get(0));
        model.addAttribute("oldEvents", eventList.get(1));
        return STUDY_EVENT_VIEW;
    }

    @GetMapping(value = {"/study/{path}/events/{id}/edit"})
    public String eventFormPage(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, Model model) {
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter();
        Study study = studyService.getStudyWithMembersAndManagers(path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(new EventRequestDto(event.getTitle(), event.getEventType(), event.getLimitOfEnrollments(),
                event.getEndEnrollmentDateTime().format(dateTimeFormatter), event.getStartDateTime().format(dateTimeFormatter),
                event.getEndDateTime().format(dateTimeFormatter),  event.getDescription()));
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(event);
        return EVENT_UPDATE_FORM_VIEW;

    }

    @PostMapping(value = {"/study/{path}/events/{id}/edit"})
    public String updateEventForm(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                  @Valid EventRequestDto eventRequestDto, Errors errors, Model model) {
        Study study = studyService.getStudyWithMembersAndManagers(path);
        Event event = eventService.getEventWithEnrollment(id);
        if (errors.hasErrors() || event.getLimitOfEnrollments() > eventRequestDto.limitOfEnrollments()) {
            model.addAttribute(study);
            model.addAttribute(account);
            model.addAttribute(event);
            return EVENT_UPDATE_FORM_VIEW;
        }
        eventService.update(account, study, event, eventRequestDto);
        return "redirect:/study/" + path + "/events/" + id;
    }

    @DeleteMapping(value = {"/study/{path}/events/{id}"})
    public String deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        eventService.deleteEvent(event, account);
        return "redirect:/study/" + path + "/events";
    }

    @PostMapping(value = {"/study/{path}/events/{id}/enroll"})
    public String enrollEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) throws IllegalAccessException {
        Study study = studyService.getStudyToEnroll(path);
        eventService.enrollEvent(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + id;
    }

    @PostMapping(value = {"/study/{path}/events/{id}/leave"})
    public String leaveEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.leaveEvent(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + id;
    }

    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/accept"})
    public String acceptEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                              @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getSimpleStudy(path, account);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/reject"})
    public String rejectEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                              @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getSimpleStudy(path, account);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/checkin"})
    public String checkinEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                               @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getSimpleStudy(path, account);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin"})
    public String cancelCheckinEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                                     @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.getSimpleStudy(path, account);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }


    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }
}