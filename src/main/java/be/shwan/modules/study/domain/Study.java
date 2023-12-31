package be.shwan.modules.study.domain;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.UserAccount;
import be.shwan.modules.study.dto.StudyDescriptionRequestDto;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private String shortDescription;

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

    private boolean published;

    private boolean recruiting;

    private boolean closed;

    private boolean useBanner;

    private int memberCount;

    public Study(String path, String title, String shortDescription, String fullDescription) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }

    public void addManager(Account account) {
        managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return published && recruiting && !closed && !members.contains(account) && !managers.contains(account);
    }
    public boolean isManager(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return members.contains(account);
    }

    public void updateDescription(StudyDescriptionRequestDto studyDescriptionRequestDto) {
        this.shortDescription = studyDescriptionRequestDto.shortDescription();
        this.fullDescription = studyDescriptionRequestDto.fullDescription();
    }

    public void enableBanner() {
        useBanner = true;
    }

    public void disableBanner() {
        useBanner = false;
    }

    public void bannerImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image == null ? "/images/default_banner.png" : image;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void addZone(Zone zone) {
        zones.add(zone);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
    }

    public void publish() {
        published = true;
        publishedDateTime = LocalDateTime.now();
    }

    public void close() {
        closed = true;
        closedDateTime = LocalDateTime.now();
    }

    public void startRecruit() {
        recruiting = true;
        recruitingUpdateDateTime = LocalDateTime.now();
    }

    public void stopRecruit() {
        recruiting = false;
        recruitingUpdateDateTime = LocalDateTime.now();
    }

    public void updatePath(String path) {
        this.path = path;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public boolean removeAble() {
        return !published;
    }

    public void join(Account account) {
        members.add(account);
        memberCount++;
    }

    public void leave(Account account) {
        members.remove(account);
        memberCount--;
    }

    public String getEncodePath() {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public boolean updatable() {
        return closed || !published;
    }

    public boolean isManager(Account account) {
        return managers.contains(account);
    }

    public boolean isMember(Account account) {
        return members.contains(account);
    }
}
