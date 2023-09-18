package com.sciencesakura.sample.infra.security;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sciencesakura.sample.domain.user.UserInfo;
import com.sciencesakura.sample.domain.user.UserRole;
import com.sciencesakura.sample.domain.user.UserService;
import com.sciencesakura.sample.domain.user.UserStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  PasswordEncoder encoder;

  @MockBean
  UserService userService;

  @Nested
  class login {

    @Test
    void success() throws Exception {
      var emailAddress = "user@example.com";
      var password = "Abc_123";
      var stubReturn = new UserInfo();
      stubReturn.setEmailAddress(emailAddress);
      stubReturn.setPassword(() -> encoder.encode(password));
      stubReturn.setStatus(UserStatus.ENABLED);
      stubReturn.setRoles(List.of(new UserRole("USER")));
      when(userService.findByEmailAddress(eq(emailAddress))).thenReturn(Optional.of(stubReturn));
      mockMvc.perform(post("/login")
              .param("email", emailAddress)
              .param("passwd", password))
          .andExpect(status().isNoContent());
    }

    @Test
    void failed_if_account_not_found() throws Exception {
      var emailAddress = "user@example.com";
      var password = "Abc_123";
      when(userService.findByEmailAddress(eq(emailAddress))).thenReturn(Optional.empty());
      mockMvc.perform(post("/login")
              .param("email", emailAddress)
              .param("passwd", password))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void failed_if_wrong_password() throws Exception {
      var emailAddress = "user@example.com";
      var password = "Abc_123";
      var stubReturn = new UserInfo();
      stubReturn.setEmailAddress(emailAddress);
      stubReturn.setPassword(() -> encoder.encode("Xyz_123"));
      stubReturn.setStatus(UserStatus.ENABLED);
      stubReturn.setRoles(List.of(new UserRole("USER")));
      when(userService.findByEmailAddress(eq(emailAddress))).thenReturn(Optional.of(stubReturn));
      mockMvc.perform(post("/login")
              .param("email", emailAddress)
              .param("passwd", password))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void failed_if_account_locked() throws Exception {
      var emailAddress = "user@example.com";
      var password = "Abc_123";
      var stubReturn = new UserInfo();
      stubReturn.setEmailAddress(emailAddress);
      stubReturn.setPassword(() -> encoder.encode(password));
      stubReturn.setStatus(UserStatus.LOCKED);
      stubReturn.setRoles(List.of(new UserRole("USER")));
      when(userService.findByEmailAddress(eq(emailAddress))).thenReturn(Optional.of(stubReturn));
      mockMvc.perform(post("/login")
              .param("email", emailAddress)
              .param("passwd", password))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void failed_if_account_disabled() throws Exception {
      var emailAddress = "user@example.com";
      var password = "Abc_123";
      var stubReturn = new UserInfo();
      stubReturn.setEmailAddress(emailAddress);
      stubReturn.setPassword(() -> encoder.encode(password));
      stubReturn.setStatus(UserStatus.DISABLED);
      stubReturn.setRoles(List.of(new UserRole("USER")));
      when(userService.findByEmailAddress(eq(emailAddress))).thenReturn(Optional.of(stubReturn));
      mockMvc.perform(post("/login")
              .param("email", emailAddress)
              .param("password", password))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  class logout {

    @Test
    @WithMockUser
    void success() throws Exception {
      mockMvc.perform(post("/logout"))
          .andExpect(status().isNoContent());
    }
  }
}
