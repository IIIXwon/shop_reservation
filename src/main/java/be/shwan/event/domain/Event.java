package be.shwan.event.domain;

import be.shwan.account.domain.Account;
import be.shwan.enrollment.domain.Enrollment;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.domain.Study;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private int limitOfEnrollment;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public Event(Account account, Study study, EventRequestDto eventRequestDto) {
        this.createBy = account;
        this.study = study;
        title = eventRequestDto.title();
        description = eventRequestDto.description();
        limitOfEnrollment = eventRequestDto.limitOfEnrollments();
        createDateTime = LocalDateTime.now();
        eventType = eventRequestDto.eventType();
        endEnrollmentDateTime = LocalDateTime.parse(eventRequestDto.endEnrollmentDateTime());
        startDateTime = LocalDateTime.parse(eventRequestDto.startDateTime());
        endDateTime = LocalDateTime.parse(eventRequestDto.endDateTime());
    }
}
