package be.shwan.modules.account.presentation.rest;

import be.shwan.infra.MockMvcTest;
import be.shwan.infra.config.AppProperties;
import be.shwan.infra.jwt.JwtTokenUtil;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.application.AccountService;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.dto.PasswordForm;
import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.application.ZoneService;
import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class RestSettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;
    @Autowired
    TagService tagService;
    @Autowired
    ZoneService zoneService;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("[POST] /api/settings/tag 선호하는 태그 추가하기")
    @Test
    void addTag() throws Exception {
        Account account = accountFactory.createDefaultAccount();
        String token = jwtTokenUtil.generateToken(account);
        RequestTagDto tagDto = new RequestTagDto("락 발라드");
        mockMvc.perform(post("/api/settings/tags")
                        .header(HttpHeaders.AUTHORIZATION, appProperties.getTokenPrefix() + " " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(tagDto))
                )
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/settings/tags"))
                .andDo(print())
        ;
        assertEquals(1, account.getTags().size());
    }

    @DisplayName("[delete] /api/settings/tag 선호하는 태그 삭제")
    @Test
    void deleteTag() throws Exception {
        Account account = accountFactory.createDefaultAccount();
        String token = jwtTokenUtil.generateToken(account);
        RequestTagDto tagDto = new RequestTagDto("락 발라드");
        Tag tag = tagService.getTag(tagDto);
        account.addTag(tag);
        mockMvc.perform(delete("/api/settings/tags")
                        .header(HttpHeaders.AUTHORIZATION, appProperties.getTokenPrefix() + " " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(tagDto))
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
        assertEquals(0, account.getTags().size());
    }

    @DisplayName("[POST] /api/settings/zones 선호하는 지역 추가하기")
    @Test
    void addZone() throws Exception {
        Account account = accountFactory.createDefaultAccount();
        String token = jwtTokenUtil.generateToken(account);
        RequestZoneDto requestZoneDto = new RequestZoneDto("Seoul(서울특별시)/none");
        mockMvc.perform(post("/api/settings/zones")
                        .header(HttpHeaders.AUTHORIZATION, appProperties.getTokenPrefix() + " " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestZoneDto))
                )
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/settings/zones"))
                .andDo(print())
        ;
        assertEquals(1, account.getZones().size());
    }

    @DisplayName("[delete] /api/settings/zones 선호하는 지역 삭제")
    @Test
    void deleteZone() throws Exception {
        Account account = accountFactory.createDefaultAccount();
        String token = jwtTokenUtil.generateToken(account);
        Zone zone = zoneService.findZone("Seoul(서울특별시)/none");
        RequestZoneDto dto = new RequestZoneDto("Seoul(서울특별시)/none");
        accountService.addZone(account, zone);
        mockMvc.perform(delete("/api/settings/zones")
                        .header(HttpHeaders.AUTHORIZATION, appProperties.getTokenPrefix() + " " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
        assertEquals(0, account.getZones().size());
    }

    @DisplayName("[POST} /api/settings/password, 비밀번호 수정하기")
    @Test
    void updatePassword() throws Exception{
        Account account = accountFactory.createDefaultAccount();
        String token = jwtTokenUtil.generateToken(account);
        PasswordForm passwordForm = new PasswordForm("87654321", "87654321");
        mockMvc.perform(post("/api/settings/password")
                        .header(HttpHeaders.AUTHORIZATION, appProperties.getTokenPrefix() + " " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordForm))
                )
                .andExpect(status().isOk())
                .andDo(print())
                ;


    }
}