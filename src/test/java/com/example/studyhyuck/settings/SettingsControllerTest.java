package com.example.studyhyuck.settings;

import com.example.studyhyuck.WithAccount;
import com.example.studyhyuck.account.AccountRepository;
import com.example.studyhyuck.account.AccountService;
import com.example.studyhyuck.account.SignUpForm;
import com.example.studyhyuck.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

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
                        post(SettingsController.SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
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
                        post(SettingsController.SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
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
                        post(SettingsController.SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
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
                        get(SettingsController.SETTINGS_PROFILE_URL)
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
                        get(SettingsController.SETTINGS_PASSWORD_URL)
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
                        post(SettingsController.SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "12345678")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
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
                        post(SettingsController.SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "1234567890")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "testHyuck9";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname(newNickname));
    }

    @WithAccount("lhg1304")
    @DisplayName("닉네임 수정하기 - 입력값 에러")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "¯\\_(ツ)_/¯";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

}