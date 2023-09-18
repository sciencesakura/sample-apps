package com.sciencesakura.sample.infra.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciencesakura.sample.domain.user.UserRole;
import java.io.Serializable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRoleDeserializerTest {

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  class deserialize {

    @Test
    void string_to_object() throws Exception {
      var json = """
          {
            "role": "USER"
          }
          """;
      var actual = objectMapper.readValue(json, DummyObject.class);
      assertThat(actual.role()).isEqualTo(new UserRole("USER"));
    }

    @Test
    void blank_string_to_null() throws Exception {
      var json = """
          {
            "role": " "
          }
          """;
      var actual = objectMapper.readValue(json, DummyObject.class);
      assertThat(actual.role()).isNull();
    }

    @Test
    void throw_exception_if_invalid_type() {
      var json = """
          {
            "role": 1
          }
          """;
      assertThatThrownBy(() -> objectMapper.readValue(json, DummyObject.class))
          .isInstanceOf(JsonMappingException.class);
    }
  }

  record DummyObject(UserRole role) implements Serializable {

  }
}
