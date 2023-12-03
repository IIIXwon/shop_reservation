package be.shwan.account.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class PersistentLogins {
    @Id
    @Column(length = 64)
    private String series;

    @Column(length = 64, nullable = false)
    private String username;

    @Column(length = 64, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime lastUsed;
}
