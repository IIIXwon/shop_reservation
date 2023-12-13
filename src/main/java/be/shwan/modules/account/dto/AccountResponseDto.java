package be.shwan.modules.account.dto;

public record AccountResponseDto(Long id, String nickname, String email, boolean active,
                                 java.util.Set<be.shwan.modules.zone.domain.Zone> zones,
                                 java.util.Set<be.shwan.modules.tag.domain.Tag> tags) {

}
