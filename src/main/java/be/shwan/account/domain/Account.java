package be.shwan.account.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

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
}
