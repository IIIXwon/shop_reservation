package be.shwan.modules.event.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvent is a Querydsl query type for Event
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvent extends EntityPathBase<Event> {

    private static final long serialVersionUID = -1484120427L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEvent event = new QEvent("event");

    public final be.shwan.modules.account.domain.QAccount createBy;

    public final DateTimePath<java.time.LocalDateTime> createDateTime = createDateTime("createDateTime", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endDateTime = createDateTime("endDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endEnrollmentDateTime = createDateTime("endEnrollmentDateTime", java.time.LocalDateTime.class);

    public final ListPath<Enrollment, QEnrollment> enrollments = this.<Enrollment, QEnrollment>createList("enrollments", Enrollment.class, QEnrollment.class, PathInits.DIRECT2);

    public final EnumPath<EventType> eventType = createEnum("eventType", EventType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> limitOfEnrollments = createNumber("limitOfEnrollments", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startDateTime = createDateTime("startDateTime", java.time.LocalDateTime.class);

    public final be.shwan.modules.study.domain.QStudy study;

    public final StringPath title = createString("title");

    public QEvent(String variable) {
        this(Event.class, forVariable(variable), INITS);
    }

    public QEvent(Path<? extends Event> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEvent(PathMetadata metadata, PathInits inits) {
        this(Event.class, metadata, inits);
    }

    public QEvent(Class<? extends Event> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createBy = inits.isInitialized("createBy") ? new be.shwan.modules.account.domain.QAccount(forProperty("createBy")) : null;
        this.study = inits.isInitialized("study") ? new be.shwan.modules.study.domain.QStudy(forProperty("study")) : null;
    }

}

