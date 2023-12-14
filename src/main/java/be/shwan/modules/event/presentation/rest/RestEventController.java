package be.shwan.modules.event.presentation.rest;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.event.application.EventService;
import be.shwan.modules.event.domain.Event;
import be.shwan.modules.event.domain.EventRepository;
import be.shwan.modules.event.dto.EventRequestDto;
import be.shwan.modules.event.dto.EventRequestDtoValidator;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class RestEventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final StudyService studyService;

    private final EventRequestDtoValidator eventRequestDtoValidator;

    private final ObjectMapper objectMapper;


    @InitBinder("eventRequestDto")
    public void setEventRequestDtoValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventRequestDtoValidator);
    }

    @PostMapping(value = {"/study/{path}/new-event"})
    public ResponseEntity newEvent(@CurrentUser Account account, @PathVariable String path, @RequestBody @Valid EventRequestDto eventRequestDto,
                                   Errors errors) throws URISyntaxException {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventService.createEvent(account, study, eventRequestDto);
        String redirect = "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
        URI uri = new URI(redirect);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = {"/study/{path}/events/{eventId}"})
    public ResponseEntity getEventInfo(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event) throws JsonProcessingException {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        Map<String, Object> result = new HashMap<>();
        result.put("study", study);
        result.put("event", event);
        return ResponseEntity.ok(objectMapper.writeValueAsString(result));
    }


    @GetMapping(value = {"/study/{path}/events"})
    public ResponseEntity getEventList(@CurrentUser Account account, @PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getStudy(path, account);
        List<List<Event>> eventList = eventService.getEventList(study);
        Map<String, Object> result = new HashMap<>();
        result.put("newEvents", eventList.get(0));
        result.put("oldEvents", eventList.get(1));
        return ResponseEntity.ok(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/study/{path}/events/{eventId}/edit"})
    public ResponseEntity updateEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
                                      @RequestBody @Valid EventRequestDto eventRequestDto, Errors errors) throws JsonProcessingException {
        Study study = studyService.getStudyWithMembersAndManagers(path, account);
        if (errors.hasErrors() || event.getLimitOfEnrollments() > eventRequestDto.limitOfEnrollments()) {
            return ResponseEntity.badRequest().build();
        }
        eventService.update(account, study, event, eventRequestDto);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임이 수정됐습니다.");
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @DeleteMapping(value = {"/study/{path}/events/{eventId}"})
    public ResponseEntity deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event) throws JsonProcessingException {
        eventService.deleteEvent(event, account);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임이 취소됐습니다.");
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/study/{path}/events/{eventId}/enroll"})
    public ResponseEntity enrollEvent(@CurrentUser Account account, @PathVariable String path,
                                      @PathVariable("eventId") Event event) throws IllegalAccessException, JsonProcessingException {
        studyService.getStudyToEnroll(path, account);
        eventService.enrollEvent(event, account);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임에 참가했습니다.");
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }

    @PostMapping(value = {"/study/{path}/events/{eventId}/leave"})
    public ResponseEntity leaveEvent(@CurrentUser Account account, @PathVariable String path,
                                     @PathVariable("eventId") Event event) throws JsonProcessingException {
        studyService.getStudyToEnroll(path, account);
        eventService.leaveEvent(event, account);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임 참가를 취소했습니다.");
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }
//
//    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/accept"})
//    public String acceptEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
//                              @PathVariable("enrollmentId") Enrollment enrollment) {
//        Study study = studyService.getSimpleStudy(path, account);
//        eventService.acceptEnrollment(event, enrollment);
//        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
//    }
//
//    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/reject"})
//    public String rejectEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
//                              @PathVariable("enrollmentId") Enrollment enrollment) {
//        Study study = studyService.getSimpleStudy(path, account);
//        eventService.rejectEnrollment(event, enrollment);
//        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
//    }
//
//    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/checkin"})
//    public String checkinEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
//                               @PathVariable("enrollmentId") Enrollment enrollment) {
//        Study study = studyService.getSimpleStudy(path, account);
//        eventService.checkInEnrollment(enrollment);
//        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
//    }
//
//    @GetMapping(value = {"/study/{path}/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin"})
//    public String cancelCheckinEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event,
//                                     @PathVariable("enrollmentId") Enrollment enrollment) {
//        Study study = studyService.getSimpleStudy(path, account);
//        eventService.cancelCheckInEnrollment(enrollment);
//        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
//    }


    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }
}