package be.shwan.account.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;

    private String emailCheckToken;

    private LocalDate createDate;
    private LocalDate lastUpdateDate;

    private boolean active;

    public Account(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.createDate = LocalDate.now();
        this.lastUpdateDate = LocalDate.now();
        this.active = false;
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }
}
