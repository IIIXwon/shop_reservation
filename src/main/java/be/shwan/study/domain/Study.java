package be.shwan.study.domain;

import be.shwan.account.domain.Account;
import be.shwan.account.domain.UserAccount;
import be.shwan.study.dto.StudyDescriptionRequestDto;
import be.shwan.tag.domain.Tag;
import be.shwan.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@NamedEntityGraph(name = "Study.tagAndManager", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.zoneAndManager", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.manager", attributeNodes = {
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withMembersAndManagers", attributeNodes = {
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
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
    }

    public void leave(Account account) {
        members.remove(account);
    }
}
