package com.sciencesakura.sample.infra.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PasswordConverterTest {

  final PasswordConverter converter = new PasswordConverter();

  @Nested
  class convertToDatabaseColumn {

    @Test
    void object_to_string() {
      assertThat(converter.convertToDatabaseColumn(() -> "Abc_123")).isEqualTo("Abc_123");
    }

    @Test
    void null_to_null() {
      assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }
  }

  @Nested
  class convertToEntityAttribute {

    @Test
    void string_to_object() {
      var actual = converter.convertToEntityAttribute("Abc_123");
      assertThat(actual).satisfies(
          p -> assertThat(p.encodedValue()).isEqualTo("Abc_123"),
          p -> assertThat(p.toString()).matches("^\\*+$")
      );
    }

    @Test
    void blank_string_to_null() {
      assertThat(converter.convertToEntityAttribute(null)).isNull();
      assertThat(converter.convertToEntityAttribute("")).isNull();
      assertThat(converter.convertToEntityAttribute(" ")).isNull();
    }
  }
}
