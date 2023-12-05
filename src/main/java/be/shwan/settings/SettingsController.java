package be.shwan.settings;

import be.shwan.account.application.AccountService;
import be.shwan.account.domain.Account;
import be.shwan.account.domain.AccountRepository;
import be.shwan.account.domain.CurrentUser;
import be.shwan.account.dto.SignUpFormDto;
import be.shwan.account.dto.SignUpFormValidator;
import be.shwan.settings.dto.*;
import be.shwan.tag.domain.Tag;
import be.shwan.tag.domain.TagRepository;
import be.shwan.tag.dto.RequestTagDto;
import be.shwan.zone.application.ZoneService;
import be.shwan.zone.domain.Zone;
import be.shwan.zone.domain.ZoneRepository;
import be.shwan.zone.dto.RequestZoneDto;
import be.shwan.zone.dto.ResponseZoneDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = {"/settings"})
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final TagRepository tagRepository;

    private final ZoneService zoneService;
    private final ZoneRepository zoneRepository;

    private final NicknameFormValidator nicknameFormValidator;

    private final ObjectMapper objectMapper;
    final String PROFILE_PATH = "/profile";
    final String PROFILE_VIEW = "/settings/profile";
    final String PASSWORD_VIEW = "/settings/password";
    private final String PASSWORD_PATH = "/password";
    private final String NOTIFICATION_PATH = "/notifications";
    private final String NOTIFICATION_VIEW = "/settings/notifications";
    private final String ACCOUNT_VIEW = "/settings/account";

    @InitBinder({"passwordForm"})
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());

    }

    @InitBinder("nicknameForm")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping(value = {PROFILE_PATH})
    public String profilePage(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ProfileInfo(account.getBio(), account.getUrl(), account.getOccupation(),
                account.getLocation(), account.getProfileImage()));
        return PROFILE_VIEW;
    }

    @PostMapping(value = {PROFILE_PATH})
    public String updateProfile(@CurrentUser Account account, @Valid ProfileInfo profileInfo, Errors errors,
                                Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return PROFILE_VIEW;
        }
        accountService.updateProfile(account, profileInfo);
        redirectAttributes.addFlashAttribute("message", "프로필을 수정 했습니다.");
        return "redirect:/settings/profile";
    }

    @GetMapping(value = {PASSWORD_PATH})
    public String passwordPage(Model model) {
        model.addAttribute(new PasswordForm("", ""));
        return PASSWORD_VIEW;
    }

    @PostMapping(value = {PASSWORD_PATH})
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return PASSWORD_VIEW;
        }
        accountService.updatePassword(account, passwordForm);
        redirectAttributes.addFlashAttribute("message", "비밀번호를 수정 했습니다.");
        return "redirect:/settings/password";
    }

    @GetMapping(NOTIFICATION_PATH)
    public String notificationPage(@CurrentUser Account account, Model model) {
        model.addAttribute(new Notifications(account.isStudyCreatedByEmail(), account.isStudyCreatedByWeb(), account.isStudyEnrollmentResultByEmail(),
                account.isStudyEnrollmentResultByWeb(), account.isStudyUpdatedByEmail(), account.isStudyUpdatedByWeb()));
        return NOTIFICATION_VIEW;
    }

    @PostMapping(NOTIFICATION_PATH)
    public String updateNotification(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
                                     RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return NOTIFICATION_VIEW;
        }
        accountService.updateNotification(account, notifications);
        redirectAttributes.addFlashAttribute("message", "알림을 수정 했습니다.");
        return "redirect:/settings/notifications";
    }

    @GetMapping(value = {"/account"})
    public String accountPage(@CurrentUser Account account, Model model) {
        model.addAttribute("nicknameForm", new NicknameForm(account.getNickname()));
        return ACCOUNT_VIEW;
    }

    @PostMapping(value = {"/account"})
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return ACCOUNT_VIEW;
        }
        accountService.updateAccount(account, nicknameForm);
        redirectAttributes.addFlashAttribute("message", "계정을 수정 했습니다.");
        return "redirect:/settings/account";
    }

    @GetMapping(value = {"/tags"})
    public String tagPage(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).toList();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return "/settings/tags";
    }

    @PostMapping(value = {"/tags/add"})
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody RequestTagDto tagDto) {
        Tag tag = tagRepository.findByTitle(tagDto.tagTitle());
        if (tag == null) {
            tag = tagRepository.save(new Tag(tagDto));
        }
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/tags/remove"})
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody RequestTagDto tagDto) {
        Tag tag = tagRepository.findByTitle(tagDto.tagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/zones"})
    public String zonePage(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<Zone> zones = accountService.getZones(account);
        List<ResponseZoneDto> zoneList = zones.stream().map(z -> new ResponseZoneDto(z.getCity(), z.getLocalNameOfCity(), z.getProvince())).toList();
        model.addAttribute("zones", zoneList);
        List<String> whitlist = zoneRepository.findAll().stream().map(z -> new ResponseZoneDto(z.getCity(), z.getLocalNameOfCity(), z.getProvince()).toString()).toList();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitlist));
        return "/settings/zones";
    }

    @PostMapping(value = {"/zones/add"})
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody RequestZoneDto requestZoneDto) {
        Zone zone = zoneService.findZone(requestZoneDto.zoneName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/zones/remove"})
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody RequestZoneDto requestZoneDto) {
        Zone zone = zoneService.findZone(requestZoneDto.zoneName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }
}