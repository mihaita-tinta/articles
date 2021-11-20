package com.mih.spring.magic;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AccountRepository accountRepository;
    @MockBean
    ProfileRepository profileRepository;
    @Mock
    Profile profile;

    @Test
    @WithMockUser
    public void test() throws Exception {
        String profileId = "junit-profile-123";
        when(profileRepository.getProfile(profileId)).thenReturn(profile);
        when(accountRepository.getAccounts(profile)).thenReturn(Collections.emptyList());

        mockMvc.perform(
                get("/accounts")
                        .cookie(new Cookie("profile", profileId))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

}
