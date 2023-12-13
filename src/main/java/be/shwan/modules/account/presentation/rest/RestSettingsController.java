package be.shwan.modules.account.presentation.rest;

import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import be.shwan.modules.account.dto.*;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings")
public class RestSettingsController {
    private final AccountService accountService;
    private final TagService tagService;
    private final ZoneService zoneService;
    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;

    @InitBinder("passwordForm")
    void initPasswordFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    void initNicknameValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @PostMapping("/nickname")
    public ResponseEntity updateNickname(@CurrentUser Account account, @RequestBody @Valid NicknameForm nicknameForm,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        accountService.restUpdateAccount(account, nicknameForm);
        return ResponseEntity.ok().body("닉네임이 변경되었습니다.");
    }
    @PostMapping("/profile")
    public ResponseEntity updateProfile(@CurrentUser Account account, @RequestBody @Valid ProfileInfo profileInfo,
                                        Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        accountService.updateProfile(account, profileInfo);
        return ResponseEntity.ok().body("프로필 정보가 수정되었습니다.");
    }

    @PostMapping("/password")
    public ResponseEntity updatePassword(@CurrentUser Account account, @RequestBody @Valid PasswordForm passwordForm, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        accountService.updatePassword(account, passwordForm);
        return ResponseEntity.ok().body("비밀번호가 변경되었습니다.");
    }

    @PostMapping("/tags")
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody RequestTagDto requestTagDto) throws URISyntaxException {
        Tag tag = tagService.getTag(requestTagDto);
        accountService.addTag(account, tag);
        URI uri = new URI("/settings/tags");
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/tags")
    public ResponseEntity deleteTag(@CurrentUser Account account, @RequestBody RequestTagDto requestTagDto) {
        Tag tag = tagService.getTag(requestTagDto);
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().body("선호하는 태그가 삭제되었습니다.");
    }

    @PostMapping("/zones")
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody RequestZoneDto requestZoneDto) throws URISyntaxException {
        Zone zone = zoneService.findZone(requestZoneDto.zoneName());
        accountService.addZone(account, zone);
        URI uri = new URI("/settings/zones");
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/zones")
    public ResponseEntity deleteZone(@CurrentUser Account account, @RequestBody RequestZoneDto requestZoneDto) {
        Zone zone = zoneService.findZone(requestZoneDto.zoneName());
        accountService.removeZone(account, zone);
        return ResponseEntity.ok().body("관심 지역이 삭제되었습니다.");
    }
}
