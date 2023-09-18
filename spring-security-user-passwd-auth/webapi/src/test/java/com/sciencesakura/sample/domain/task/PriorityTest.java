package com.sciencesakura.sample.domain.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PriorityTest {

  @Nested
  class static_of {

    @ParameterizedTest
    @EnumSource
    void int_to_enum(Priority constant) {
      var i = constant.value();
      assertThat(Priority.of(i)).isEqualTo(constant);
    }

    @ParameterizedTest
    @EnumSource
    void string_to_enum(Priority constant) {
      var s = constant.toString();
      assertThat(Priority.of(s)).isEqualTo(constant);
      assertThat(Priority.of(" %s ".formatted(s))).isEqualTo(constant);
    }

    @Test
    void blank_string_to_default() {
      assertThat(Priority.of(null)).isEqualTo(Priority.NORMAL);
      assertThat(Priority.of("")).isEqualTo(Priority.NORMAL);
      assertThat(Priority.of(" ")).isEqualTo(Priority.NORMAL);
    }

    @Test
    void throw_exception_if_invalid_integer() {
      assertThatThrownBy(() -> Priority.of(999))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throw_exception_if_invalid_format_string() {
      assertThatThrownBy(() -> Priority.of("X"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasCauseInstanceOf(NumberFormatException.class);
    }
  }
}
