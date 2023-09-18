package com.sciencesakura.sample.domain.task;

import com.sciencesakura.sample.domain.ValueObject;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * The enumeration type which represents a task priority.
 */
@RequiredArgsConstructor
public enum Priority implements ValueObject<Priority, Integer> {

  LOWEST(-20),

  LOWER(-10),

  NORMAL(0),

  HIGHER(10),

  HIGHEST(20);

  private final int value;

  /**
   * Returns the enum constant which has the specified value.
   *
   * @param value the internal value
   * @return the enum constant
   * @throws IllegalArgumentException if the specified value is invalid
   */
  @Nonnull
  public static Priority of(int value) {
    for (var constant : values()) {
      if (constant.value == value) {
        return constant;
      }
    }
    throw new IllegalArgumentException("invalid value: %s(%d)".formatted(Priority.class.getSimpleName(), value));
  }

  /**
   * Returns the enum constant which has the specified value.
   *
   * @param value the string representation of the internal value
   * @return the enum constant
   * @throws IllegalArgumentException if the specified value is invalid
   */
  @Nonnull
  public static Priority of(@Nullable String value) {
    var s = StringUtils.strip(value);
    if (StringUtils.isEmpty(s)) {
      return NORMAL;
    }
    try {
      return of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("invalid value: %s(%s)".formatted(Priority.class.getSimpleName(), s), e);
    }
  }

  @Nonnull
  @Override
  public Integer value() {
    return value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
