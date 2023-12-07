package be.shwan.event.domain;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.UserAccount;
import be.shwan.enrollment.domain.Enrollment;
import be.shwan.event.dto.EventRequestDto;
import be.shwan.study.domain.Study;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(name = "Event.withEnrollments", attributeNodes = {
        @NamedAttributeNode("enrollments")
})
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

    private int limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public Event(Account account, Study study, EventRequestDto eventRequestDto) {
        this.createBy = account;
        this.study = study;
        title = eventRequestDto.title();
        description = eventRequestDto.description();
        limitOfEnrollments = eventRequestDto.limitOfEnrollments();
        createDateTime = LocalDateTime.now();
        eventType = eventRequestDto.eventType();
        endEnrollmentDateTime = LocalDateTime.parse(eventRequestDto.endEnrollmentDateTime());
        startDateTime = LocalDateTime.parse(eventRequestDto.startDateTime());
        endDateTime = LocalDateTime.parse(eventRequestDto.endDateTime());
    }

    public boolean isEnrollableFor(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        if (enrollments.isEmpty()) {
            return true;
        }
        Enrollment enrollment = enrollments.get(id.intValue());
        return !account.equals(enrollment.getAccount());
    }
    public boolean isDisenrollableFor(UserAccount userAccount) {
        return !isEnrollableFor(userAccount);
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        if (enrollments.isEmpty()) {
            return false;
        }
        Enrollment enrollment = enrollments.get(id.intValue());
         return enrollment.isAttended();
    }

    public boolean isEndEvent() {
        return LocalDateTime.now().isAfter(endDateTime);
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
}
