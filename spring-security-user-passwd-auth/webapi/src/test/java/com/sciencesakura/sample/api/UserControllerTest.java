package com.sciencesakura.sample.api;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sciencesakura.sample.domain.user.Password;
import com.sciencesakura.sample.domain.user.UserInfo;
import com.sciencesakura.sample.domain.user.UserQuery;
import com.sciencesakura.sample.domain.user.UserRole;
import com.sciencesakura.sample.domain.user.UserService;
import com.sciencesakura.sample.domain.user.UserStatus;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  UserService userService;

  @Nested
  @WithMockUser(roles = "USER")
  class GET_users {

    @Test
    void return_200_OK() throws Exception {
      var stubArg = new UserQuery();
      stubArg.setText("text");
      stubArg.setStatus(Set.of(UserStatus.TEMPORARY, UserStatus.ENABLED));
      stubArg.setPage(2);
      stubArg.setSize(30);
      stubArg.setOrder(List.of(Order.by("name"), Order.by("emailAddress")));
      var stubReturn = new UserInfo();
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      stubReturn.setEmailAddress("user@example.com");
      stubReturn.setPassword(() -> "Abc_123");
      stubReturn.setName("user");
      stubReturn.setStatus(UserStatus.ENABLED);
      stubReturn.setDescription("description");
      stubReturn.setRoles(List.of(new UserRole("A"), new UserRole("B")));
      when(userService.findAll(eq(stubArg))).thenReturn(new PageImpl<>(List.of(stubReturn)));

      mockMvc.perform(get("/users")
              .param("text", "text")
              .param("status", "0", "1")
              .param("page", "2")
              .param("size", "30")
              .param("order", "name", "emailAddress"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              [
                {
                  "id": "00000000-0000-0000-0000-000000000001",
                  "emailAddress": "user@example.com",
                  "name": "user",
                  "status": 1,
                  "description": "description",
                  "roles": ["A", "B"]
                }
              ]
              """));
    }
  }

  @Nested
  @WithMockUser(roles = "USER")
  class GET_users_id {

    @Test
    void return_200_OK() throws Exception {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var stubReturn = new UserInfo();
      stubReturn.setId(id);
      stubReturn.setEmailAddress("user@example.com");
      stubReturn.setPassword(() -> "Abc_123");
      stubReturn.setName("user");
      stubReturn.setStatus(UserStatus.ENABLED);
      stubReturn.setDescription("description");
      stubReturn.setRoles(List.of(new UserRole("A"), new UserRole("B")));
      when(userService.findById(eq(id))).thenReturn(Optional.of(stubReturn));

      mockMvc.perform(get("/users/{id}", id))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              {
                "id": "00000000-0000-0000-0000-000000000001",
                "emailAddress": "user@example.com",
                "name": "user",
                "status": 1,
                "description": "description",
                "roles": ["A", "B"]
              }
              """));
    }

    @Test
    void return_404_NOT_FOUND() throws Exception {
      var stubArg = UUID.fromString("00000000-0000-0000-0000-000000000001");
      when(userService.findById(eq(stubArg))).thenReturn(Optional.empty());

      mockMvc.perform(get("/users/{id}", "00000000-0000-0000-0000-000000000001"))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.messages", hasSize(1)))
          .andExpect(jsonPath("$.messages[0].message").isString())
          .andExpect(jsonPath("$.messages[0].details.resource").value("UserInfo"))
          .andExpect(jsonPath("$.messages[0].details.identifier").value("00000000-0000-0000-0000-000000000001"));
    }
  }

  @Nested
  @WithMockUser(roles = "ADMIN")
  class POST_users {

    @Test
    void return_201_CREATED() throws Exception {
      var stubArg = new UserInfo();
      stubArg.setEmailAddress("user@example.com");
      stubArg.setPassword(new DummyPassword("Abc_123"));
      stubArg.setName("user");
      stubArg.setStatus(UserStatus.ENABLED);
      stubArg.setDescription("description");
      stubArg.setRoles(List.of(new UserRole("A"), new UserRole("B")));
      var stubReturn = stubArg.clone();
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      when(userService.create(eq(stubArg))).thenReturn(stubReturn);

      mockMvc.perform(post("/s/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "emailAddress": "user@example.com",
                    "password": "Abc_123",
                    "name": "user",
                    "status": 1,
                    "description": "description",
                    "roles": ["A", "B"]
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", endsWith("/s/users/00000000-0000-0000-0000-000000000001")));
    }
  }

  @Nested
  @WithMockUser(roles = "ADMIN")
  class PUT_users_id {

    @Test
    void return_204_NO_CONTENT() throws Exception {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var stubArg = new UserInfo();
      stubArg.setId(id);
      stubArg.setEmailAddress("user@example.com");
      stubArg.setPassword(new DummyPassword("Abc_123"));
      stubArg.setName("user");
      stubArg.setStatus(UserStatus.ENABLED);
      stubArg.setDescription("description");
      stubArg.setRoles(List.of(new UserRole("A"), new UserRole("B")));
      var stubReturn = stubArg.clone();
      when(userService.update(eq(id), eq(stubArg))).thenReturn(stubReturn);

      mockMvc.perform(put("/s/users/{id}", id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "id": "00000000-0000-0000-0000-000000000001",
                    "emailAddress": "user@example.com",
                    "password": "Abc_123",
                    "name": "user",
                    "status": 1,
                    "description": "description",
                    "roles": ["A", "B"]
                  }
                  """))
          .andExpect(status().isNoContent());
    }
  }

  record DummyPassword(String value) implements Password {

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Password p) {
        return Objects.equals(p.value(), value);
      }
      return false;
    }
  }
}
