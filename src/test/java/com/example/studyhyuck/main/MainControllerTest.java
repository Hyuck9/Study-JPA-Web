package com.example.studyhyuck.main;

import com.example.studyhyuck.account.AccountRepository;
import com.example.studyhyuck.account.AccountService;
import com.example.studyhyuck.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountService accountService;
    @Autowired private AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("lhg1304");
        signUpForm.setEmail("lhg1304@naver.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("이메일로 로그인 성공")
    @Test
    public void login_with_email() throws Exception {
        this.mockMvc
                .perform(
                        post("/login")
                                .param("username", "lhg1304@naver.com")
                                .param("password", "12345678")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("lhg1304"))
        ;
    }

    @DisplayName("닉네임으로 로그인 성공")
    @Test
    public void login_with_nickname() throws Exception {
        this.mockMvc
                .perform(
                        post("/login")
                                .param("username", "lhg1304")
                                .param("password", "12345678")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("lhg1304"))
        ;
    }

    @DisplayName("로그인 실패")
    @Test
    public void login_fail() throws Exception {
        this.mockMvc
                .perform(
                        post("/login")
                                .param("username", "12345")
                                .param("password", "12345678")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated())
        ;
    }

    @WithMockUser
    @DisplayName("로그아웃")
    @Test
    public void logout() throws Exception {
        this.mockMvc
                .perform(
                        post("/logout")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated())
        ;
    }

}