package com.sciencesakura.sample.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort.Order;

class OrderConverterTest {

  final OrderConverter converter = new OrderConverter();

  @Nested
  class convert {

    @Test
    void string_to_object() {
      assertThat(converter.convert("foo")).isEqualTo(Order.by("foo"));
      assertThat(converter.convert("foo:asc")).isEqualTo(Order.asc("foo"));
      assertThat(converter.convert("foo:desc")).isEqualTo(Order.desc("foo"));

      assertThat(converter.convert(" foo ")).isEqualTo(Order.by("foo"));
      assertThat(converter.convert(" foo : asc ")).isEqualTo(Order.asc("foo"));
      assertThat(converter.convert(" foo : desc ")).isEqualTo(Order.desc("foo"));
    }

    @Test
    void blank_string_to_null() {
      assertThat(converter.convert("")).isNull();
      assertThat(converter.convert(" ")).isNull();
    }

    @Test
    void throw_exception_if_invalid_format_string() {
      assertThatThrownBy(() -> converter.convert(":"))
          .isInstanceOf(IllegalArgumentException.class);
      assertThatThrownBy(() -> converter.convert("foo:bar"))
          .isInstanceOf(IllegalArgumentException.class);
      assertThatThrownBy(() -> converter.convert("foo:bar:baz"))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }
}
