package be.shwan.account.domain;

import be.shwan.settings.dto.NicknameForm;
import be.shwan.settings.dto.Notifications;
import be.shwan.settings.dto.ProfileInfo;
import be.shwan.study.domain.Study;
import be.shwan.tag.domain.Tag;
import be.shwan.zone.domain.Zone;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    private LocalDateTime emailCheckTokenIssueTime;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;
    private String bio;
    private String url;
    private String occupation;
    private String location;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb = true;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb = true;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb = true;

    private LocalDate createDate;
    private LocalDate lastUpdateDate;

    private boolean active;
    private String emailLoginToken;
    private LocalDateTime emailLoginTokenIssueTime;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

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

    public void verify() {
        active = true;
    }

    public boolean isValidToken(String token) {
        return emailCheckToken.equals(token);
    }

    public void sendEmailCheckToken() {
        emailCheckTokenIssueTime = LocalDateTime.now();
    }

    public boolean isValidIssueTokenTime() {
        return LocalDateTime.now().isAfter(emailCheckTokenIssueTime.plusHours(1L));
    }

    public void updateProfile(ProfileInfo profileInfo) {

        this.bio = profileInfo.bio();
        this.url = profileInfo.url();
        this.occupation = profileInfo.occupation();
        this.location = profileInfo.location();
        this.profileImage = profileInfo.profileImage();
    }

    public void updatePassword(String encodePassword) {
        this.password = encodePassword;
    }

    public void updateNotification(Notifications notifications) {
        this.studyCreatedByEmail = notifications.studyCreatedByEmail();
        this.studyCreatedByWeb = notifications.studyCreatedByWeb();
        this.studyEnrollmentResultByEmail = notifications.studyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = notifications.studyEnrollmentResultByWeb();
        this.studyUpdatedByEmail = notifications.studyUpdatedByEmail();
        this.studyUpdatedByWeb = notifications.studyUpdatedByWeb();
    }

    public void updateAccount(NicknameForm signUpFormDto) {
        this.nickname = signUpFormDto.nickname();
    }

    public void issueEmailLoginToken() {
        this.emailLoginToken = UUID.randomUUID().toString();
        this.emailLoginTokenIssueTime = LocalDateTime.now();
    }

    public boolean isValidEmailLoginToken(String token) {
        return this.emailLoginToken.equals(token);
    }

    public boolean isValidEmailLoginToken() {
        return LocalDateTime.now().isAfter(emailLoginTokenIssueTime.plusHours(1L));
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    public void addZone(Zone zone) {
        zones.add(zone);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
    }

    public boolean isManagerOf(Study study) {
        return study.getManagers().contains(this);
    }
}
