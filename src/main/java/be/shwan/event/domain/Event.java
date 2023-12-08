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

@NamedEntityGraph(name = "EventAll.withEnrollments", attributeNodes = {
        @NamedAttributeNode("enrollments")
})
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
        for(Enrollment enrollment : enrollments) {
            if(account.equals(enrollment.getAccount())) {
                return false;
            }
        }
        return true;
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {
        return !isEnrollableFor(userAccount);
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        if (enrollments.isEmpty()) {
            return false;
        }
        Enrollment enrollment = enrollments.stream().filter(e -> e.getEvent().equals(this)).findFirst().orElseThrow();
        return enrollment.isAttended();
    }

    public boolean isEndEvent() {
        return LocalDateTime.now().isAfter(endDateTime);
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public void update(EventRequestDto eventRequestDto) {
        title = eventRequestDto.title();
        description = eventRequestDto.description();
        limitOfEnrollments = eventRequestDto.limitOfEnrollments();
        createDateTime = LocalDateTime.now();
        endEnrollmentDateTime = LocalDateTime.parse(eventRequestDto.endEnrollmentDateTime());
        startDateTime = LocalDateTime.parse(eventRequestDto.startDateTime());
        endDateTime = LocalDateTime.parse(eventRequestDto.endDateTime());

        if (limitOfEnrollments < eventRequestDto.limitOfEnrollments()) {
            if (eventType == EventType.FCFS) {
                updateEnrollAccepted(numberOfRemainSpots());
            }
        }

    }

    public void enroll(Enrollment enrollment) {
        if (numberOfRemainSpots() > 0) {
            enrollment.accept();
        }
        enrollments.add(enrollment);
        enrollment.updateEvent(this);
    }

    public void leaveEnrollByFCFS(Event event, Account account) {
        Enrollment enrollment = enrollments.stream().filter(e -> e.getAccount().equals(account)).findFirst().orElseThrow();
        enrollments.remove(enrollment);

        if (numberOfRemainSpots() > 0 && !enrollments.isEmpty()) {
            updateEnrollAccepted(numberOfRemainSpots());
        }

    }

    private void updateEnrollAccepted(int max) {
        int addCount = 0;
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment nestEnrollment = enrollments.get(i);
            if (!nestEnrollment.isAccepted()) {
                nestEnrollment.accept();
                addCount++;
                nestEnrollment.updateEvent(this);
            }

            if (addCount >= max) {
                return;
            }
        }
    }

    public boolean isAbleToAcceptWaitingEnrollment() {
        return eventType == EventType.FCFS && numberOfRemainSpots() > 0;
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.addEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.removeEvent();
    }

    private Enrollment getTheFirstWaitingEnrollment() {
        for(Enrollment element: enrollments) {
            if(!element.isAccepted()){
                return element;
            }
        }
        return null;
    }

    public void acceptNextWaitingEnrollment() {
        if (isAbleToAcceptWaitingEnrollment()) {
            Enrollment enrollmentToAccepted = getTheFirstWaitingEnrollment();
            if (enrollmentToAccepted != null) {
                enrollmentToAccepted.accept();
            }
        }
    }

    public void acceptNextWaitingEnrollmentList() {
        if (isAbleToAcceptWaitingEnrollment()) {
            List<Enrollment> waitingEnrollments = getWaitingEnrollments();
            int min = Math.min(numberOfRemainSpots(), waitingEnrollments.size());
            waitingEnrollments.subList(0, min).forEach(Enrollment::accept);
        }
    }

    private List<Enrollment> getWaitingEnrollments() {
        return enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).toList();
    }
}
