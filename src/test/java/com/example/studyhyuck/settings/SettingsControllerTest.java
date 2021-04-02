package com.example.studyhyuck.settings;

import com.example.studyhyuck.WithAccount;
import com.example.studyhyuck.account.AccountRepository;
import com.example.studyhyuck.account.AccountService;
import com.example.studyhyuck.account.SignUpForm;
import com.example.studyhyuck.domain.Account;
import com.example.studyhyuck.domain.Tag;
import com.example.studyhyuck.settings.form.TagForm;
import com.example.studyhyuck.tag.TagRepository;
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
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "Hyuck9", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 정상1")
    @Test
    public void updateProfileWithUserDetails() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        this.mockMvc
                .perform(
                        post(SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_PROFILE_URL))
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
                        post(SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_PROFILE_URL))
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
                        post(SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PROFILE_VIEW_NAME))
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
                        get(SETTINGS_PROFILE_URL)
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
                        get(SETTINGS_PASSWORD_URL)
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
                        post(SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "12345678")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_PASSWORD_URL))
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
                        post(SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "1234567890")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(SETTINGS_ACCOUNT_URL))
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
                    post(SETTINGS_ACCOUNT_URL)
                        .param("nickname", newNickname)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_ACCOUNT_URL))
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
                    post(SETTINGS_ACCOUNT_URL)
                        .param("nickname", newNickname)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("lhg1304")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SETTINGS_TAGS_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_TAGS_VIEW_NAME))
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
                    post(SETTINGS_TAGS_URL + "/add")
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
                    post(SETTINGS_TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf())
                )
                .andExpect(status().isOk());

        assertFalse(lhg1304.getTags().contains(newTag));
    }

}