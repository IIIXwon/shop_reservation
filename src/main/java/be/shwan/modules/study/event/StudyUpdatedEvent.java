package be.shwan.modules.study.event;

import be.shwan.modules.study.domain.Study;

public record StudyUpdatedEvent(Study study, String message) {
}
