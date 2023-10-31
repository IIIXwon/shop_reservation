package be.shwan.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private String firstName;
    private String lastName;
    @Embedded
    private Email email;

    private LocalDate createDate;
    private LocalDate lastUpdateDate;

    private boolean active;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public Account(String lastName, String firstName, Email email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }
}
