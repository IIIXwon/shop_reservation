package be.shwan.account.domain;

import be.shwan.settings.dto.ProfileInfo;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Lob
    private String profileImage;
    private String bio;
    private String url;
    private String occupation;
    private String location;

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
}
