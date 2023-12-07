package be.shwan.event.dto;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventRequestDtoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventRequestDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventRequestDto requestDto = (EventRequestDto) target;
        LocalDateTime endEnrollmentDateTime = getLocalDateTime(requestDto.endEnrollmentDateTime());
        LocalDateTime startDateTime = getLocalDateTime(requestDto.startDateTime());
        LocalDateTime endDateTime = getLocalDateTime(requestDto.endDateTime());

        if (endEnrollmentDateTime.isAfter(endDateTime)) {
            errors.rejectValue("errors", "wrong.endEnrollmentDateTime", "시간을 확인 해주세요.");
        }
        if (startDateTime.isAfter(endDateTime)) {
            errors.rejectValue("errors", "wrong.startDateTime", "시간을 확인 해주세요.");
        }

        if (endDateTime.isBefore(startDateTime)) {
            errors.rejectValue("errors", "wrong.endDateTime", "시간을 확인 해주세요.");
        }

    }

    private LocalDateTime getLocalDateTime(String localDatetime) {
        return LocalDateTime.parse(localDatetime);
    }
}
