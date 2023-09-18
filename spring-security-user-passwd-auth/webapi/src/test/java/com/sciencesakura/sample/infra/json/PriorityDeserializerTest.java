package com.sciencesakura.sample.infra.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciencesakura.sample.domain.task.Priority;
import java.io.Serializable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PriorityDeserializerTest {

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  class deserialize {

    @ParameterizedTest
    @EnumSource
    void int_to_enum(Priority constant) throws Exception {
      var json = """
          {
            "priority": %d
          }
          """.formatted(constant.value());
      var actual = objectMapper.readValue(json, DummyObject.class);
      assertThat(actual.priority()).isEqualTo(constant);
    }

    @ParameterizedTest
    @EnumSource
    void string_to_enum(Priority constant) throws Exception {
      var json = """
          {
            "priority": "%d"
          }
          """.formatted(constant.value());
      var actual = objectMapper.readValue(json, DummyObject.class);
      assertThat(actual.priority()).isEqualTo(constant);
    }

    @Test
    void throw_exception_if_invalid_type() {
      var json = """
          {
            "priority": []
          }
          """;
      assertThatThrownBy(() -> objectMapper.readValue(json, DummyObject.class))
          .isInstanceOf(JsonMappingException.class);
    }
  }

  record DummyObject(Priority priority) implements Serializable {

  }
}
