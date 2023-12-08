package be.shwan.modules.study.dto;

import be.shwan.modules.study.domain.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyPathRequestDtoValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(StudyPathRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyPathRequestDto requestDto = (StudyPathRequestDto) target;
        if (studyRepository.existsByPath(requestDto.newPath())) {
            errors.rejectValue("path", "worng.path", "스터디 경로값을 사용할 수 없습니다.");
        }
    }
}
