package be.shwan.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

@NoArgsConstructor
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
        this.lastName = checkEmpty(lastName);
        this.firstName = firstName;
        this.email = email;
        this.createDate = LocalDate.now();
        this.lastUpdateDate = LocalDate.now();
        this.active = false;
    }

    private String checkEmpty(String str) {
        if(ObjectUtils.isEmpty(str)) {
            throw new IllegalArgumentException("필수 입력 항목입니다.");
        }
        return str;
    }
}
