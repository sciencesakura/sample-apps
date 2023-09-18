package com.sciencesakura.sample.infra.json;

import static com.sciencesakura.sample.test.TestUtils.matchedPassword;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciencesakura.sample.domain.user.Password;
import java.io.Serializable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class PasswordDeserializerTest {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  PasswordEncoder encoder;

  @Nested
  class deserialize {

    @Test
    void string_to_object() throws Exception {
      var rawPassword = "Abc_123";
      var json = """
          {
            "password": "%s"
          }
          """.formatted(rawPassword);
      var actual = objectMapper.readValue(json, DummyObject.class);
      assertThat(actual.password()).satisfies(
          p -> assertThat(p.encodedValue()).is(matchedPassword(rawPassword, encoder)),
          p -> assertThat(p.toString()).matches("^\\*+$")
      );
    }

    @Test
    void blank_string_to_null() throws Exception {
      var json = """
          {
            "password": " "
          }
          """;
      var actual = objectMapper.readValue(json, DummyObject.class);
      assertThat(actual.password()).isNull();
    }

    @Test
    void throw_exception_if_invalid_type() {
      var json = """
          {
            "password": 1
          }
          """;
      assertThatThrownBy(() -> objectMapper.readValue(json, DummyObject.class))
          .isInstanceOf(JsonMappingException.class);
    }
  }

  record DummyObject(Password password) implements Serializable {

  }
}
