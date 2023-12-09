package be.shwan.modules.event.event;

import be.shwan.modules.event.domain.Enrollment;

public record EnrollmentEvent(Enrollment enrollment, String message){
}
