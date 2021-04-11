package com.example.studyhyuck.settings;

import com.example.studyhyuck.account.WithAccount;
import com.example.studyhyuck.account.AccountRepository;
import com.example.studyhyuck.account.AccountService;
import com.example.studyhyuck.account.SignUpForm;
import com.example.studyhyuck.domain.Account;
import com.example.studyhyuck.domain.Tag;
import com.example.studyhyuck.domain.Zone;
import com.example.studyhyuck.settings.form.TagForm;
import com.example.studyhyuck.settings.form.ZoneForm;
import com.example.studyhyuck.tag.TagRepository;
import com.example.studyhyuck.zone.ZoneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.example.studyhyuck.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired ZoneRepository zoneRepository;
    @Autowired TagRepository tagRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("Hyuck9");
        signUpForm.setEmail("lhg1304@naver.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithUserDetails(value = "Hyuck9", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 정상1")
    @Test
    public void updateProfileWithUserDetails() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        this.mockMvc
                .perform(
                        post(ROOT + SETTINGS + PROFILE)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"))
        ;
        Account lhg1304 = accountRepository.findByNickname("Hyuck9");
        assertEquals(bio, lhg1304.getBio());
    }

    @WithAccount(value = "lhg1304")
    @DisplayName("프로필 수정하기 - 입력값 정상2")
    @Test
    public void updateProfileWithAccount() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        this.mockMvc
                .perform(
                        post(ROOT + SETTINGS + PROFILE)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"))
        ;
        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        assertEquals(bio, lhg1304.getBio());
    }

    @WithAccount(value = "lhg1304")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    public void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 아주아주 길게 소개를 수정하는 경우.";
        this.mockMvc
                .perform(
                        post(ROOT + SETTINGS + PROFILE)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;
        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        assertNull(lhg1304.getBio());
    }

    @WithAccount(value = "lhg1304")
    @DisplayName("프로필 수정 폼")
    @Test
    public void updateProfileForm() throws Exception {
        this.mockMvc
                .perform(
                        get(ROOT + SETTINGS + PROFILE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
        ;
    }

    @WithAccount(value = "lhg1304")
    @DisplayName("패스워드 수정 폼")
    @Test
    public void updatePasswordForm() throws Exception {
        this.mockMvc
                .perform(
                        get(ROOT + SETTINGS + PASSWORD)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @WithAccount(value = "lhg1304")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    public void updatePasswordSuccess() throws Exception {
        this.mockMvc
                .perform(
                        post(ROOT + SETTINGS + PASSWORD)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "12345678")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"))
        ;
        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        assertTrue(passwordEncoder.matches("12345678", lhg1304.getPassword()));
    }

    @WithAccount(value = "lhg1304")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    public void updatePasswordFail() throws Exception {
        this.mockMvc
                .perform(
                        post(ROOT + SETTINGS + PASSWORD)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "1234567890")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "testHyuck9";
        mockMvc
                .perform(
                    post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname(newNickname));
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정하기 - 입력값 에러")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "¯\\_(ツ)_/¯";
        mockMvc
                .perform(
                    post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("lhg1304")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("lhg1304")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc
                .perform(
                    post(ROOT + SETTINGS + TAGS + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf())
                )
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        assertTrue(lhg1304.getTags().contains(newTag));
    }

    @WithAccount("lhg1304")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(lhg1304, newTag);

        assertTrue(lhg1304.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc
                .perform(
                        post(ROOT + SETTINGS + TAGS + "/remove")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagForm))
                            .with(csrf())
                )
                .andExpect(status().isOk());

        assertFalse(lhg1304.getTags().contains(newTag));
    }

    private final Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

    @WithAccount("lhg1304")
    @DisplayName("계정의 지역 정보 수정 폼")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("lhg1304")
    @DisplayName("계정의 지역 정보 추가")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc
                .perform(
                        post(ROOT + SETTINGS + ZONES + "/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(zoneForm))
                            .with(csrf())
                )
                .andExpect(status().isOk());

        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(lhg1304.getZones().contains(zone));
    }

    @WithAccount("lhg1304")
    @DisplayName("계정의 지역 정보 삭제")
    @Test
    void removeZone() throws Exception {
        Account lhg1304 = accountRepository.findByNickname("lhg1304");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(lhg1304, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc
                .perform(
                        post(ROOT + SETTINGS + ZONES + "/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(zoneForm))
                                .with(csrf())
                )
                .andExpect(status().isOk());

        assertFalse(lhg1304.getZones().contains(zone));
    }
}