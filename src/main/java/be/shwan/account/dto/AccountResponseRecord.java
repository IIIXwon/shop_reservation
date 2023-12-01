package be.shwan.account.dto;

import lombok.Builder;

public record AccountResponseRecord(Long id, String nickname, String email, boolean active) {
    @Builder
    public AccountResponseRecord(Long id, String nickname, String email, boolean active) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.active = active;
    }
}
