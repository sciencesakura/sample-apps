package com.sciencesakura.sample.domain.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TaskStatusTest {

  @Nested
  class static_of {

    @ParameterizedTest
    @EnumSource
    void int_to_enum(TaskStatus constant) {
      var i = constant.value();
      assertThat(TaskStatus.of(i)).isEqualTo(constant);
    }

    @ParameterizedTest
    @EnumSource
    void string_to_enum(TaskStatus constant) {
      var s = constant.toString();
      assertThat(TaskStatus.of(s)).isEqualTo(constant);
      assertThat(TaskStatus.of(" %s ".formatted(s))).isEqualTo(constant);
    }

    @Test
    void blank_string_to_default() {
      assertThat(TaskStatus.of(null)).isEqualTo(TaskStatus.CREATED);
      assertThat(TaskStatus.of("")).isEqualTo(TaskStatus.CREATED);
      assertThat(TaskStatus.of(" ")).isEqualTo(TaskStatus.CREATED);
    }

    @Test
    void throw_exception_if_invalid_integer() {
      assertThatThrownBy(() -> TaskStatus.of(999))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throw_exception_if_invalid_format_string() {
      assertThatThrownBy(() -> TaskStatus.of("X"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasCauseInstanceOf(NumberFormatException.class);
    }
  }
}
