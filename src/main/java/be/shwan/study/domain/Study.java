package be.shwan.study.domain;

import be.shwan.account.domain.Account;
import be.shwan.tag.domain.Tag;
import be.shwan.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    Set<Account> managers = new HashSet<>();

    @ManyToMany
    Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shotDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean closed;

    private boolean useBanner;

    public Study(String path, String title, String shotDescription, String fullDescription) {
        this.path = path;
        this.title = title;
        this.shotDescription = shotDescription;
        this.fullDescription = fullDescription;
    }

    public void addManager(Account account) {
        managers.add(account);
    }
}
