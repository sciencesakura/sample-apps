package com.sciencesakura.sample.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class UserStatusTest {

  @Nested
  class static_of {

    @ParameterizedTest
    @EnumSource
    void int_to_enum(UserStatus constant) {
      var i = constant.value();
      assertThat(UserStatus.of(i)).isEqualTo(constant);
    }

    @ParameterizedTest
    @EnumSource
    void string_to_enum(UserStatus constant) {
      var s = constant.toString();
      assertThat(UserStatus.of(s)).isEqualTo(constant);
      assertThat(UserStatus.of(" %s ".formatted(s))).isEqualTo(constant);
    }

    @Test
    void blank_string_to_default() {
      assertThat(UserStatus.of(null)).isEqualTo(UserStatus.TEMPORARY);
      assertThat(UserStatus.of("")).isEqualTo(UserStatus.TEMPORARY);
      assertThat(UserStatus.of(" ")).isEqualTo(UserStatus.TEMPORARY);
    }

    @Test
    void throw_exception_if_invalid_integer() {
      assertThatThrownBy(() -> UserStatus.of(999))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throw_exception_if_invalid_format_string() {
      assertThatThrownBy(() -> UserStatus.of("X"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasCauseInstanceOf(NumberFormatException.class);
    }
  }
}
