package com.sciencesakura.sample.infra;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort.Order;

/**
 * The converter that maps a {@code String} to an {@code Order}.
 */
public class OrderConverter implements Converter<String, Order> {

  @Override
  public Order convert(@Nonnull String source) {
    var s = StringUtils.strip(source);
    if (StringUtils.isEmpty(s)) {
      return null;
    }
    var parts = StringUtils.split(s, ':');
    if (parts.length == 1) {
      return Order.by(parts[0]);
    }
    if (parts.length == 2) {
      var property = parts[0].stripTrailing();
      return switch (parts[1].stripLeading().toLowerCase()) {
        case "asc" -> Order.asc(property);
        case "desc" -> Order.desc(property);
        default -> throw new IllegalArgumentException("invalid value: %s".formatted(s));
      };
    }
    throw new IllegalArgumentException("invalid value: %s".formatted(s));
  }
}
