package be.shwan.enrollment.domain;

import be.shwan.account.domain.Account;
import be.shwan.event.domain.Event;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NamedEntityGraph(name = "Enrollment.withEventAndAccount", attributeNodes = {
        @NamedAttributeNode("event"),
        @NamedAttributeNode("account")
})
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrollAt;

    private boolean accepted;

    private boolean attended;

    public Enrollment(Account account, Event event) {
        this.event =  event;
        this.account = account;
        enrollAt = LocalDateTime.now();
        accepted = event.isAbleToAcceptWaitingEnrollment();
    }

    public void accept() {
        this.accepted = true;
    }

    public void updateEvent(Event event) {
        this.event = event;
    }

    public void addEvent(Event event) {
        this.event = event;
    }

    public void removeEvent() {
        this.event = null;
    }

    public void reject() {
        accepted = false;
    }

    public void attend() {
        attended = true;
    }

    public void cancelAttend() {
        attended = false;
    }
}
