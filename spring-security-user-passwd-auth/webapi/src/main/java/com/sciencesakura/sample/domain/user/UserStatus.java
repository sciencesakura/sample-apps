package com.sciencesakura.sample.domain.user;

import com.sciencesakura.sample.domain.ValueObject;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * The enumeration type which represents a user status.
 */
@RequiredArgsConstructor
public enum UserStatus implements ValueObject<UserStatus, Integer> {

  TEMPORARY(0),

  ENABLED(1),

  LOCKED(8),

  DISABLED(9);

  private final int value;

  /**
   * Returns the enum constant which has the specified value.
   *
   * @param value the internal value
   * @return the enum constant
   * @throws IllegalArgumentException if the specified value is invalid
   */
  @Nonnull
  public static UserStatus of(int value) {
    for (var constant : values()) {
      if (constant.value == value) {
        return constant;
      }
    }
    throw new IllegalArgumentException("invalid value: %s(%d)".formatted(UserStatus.class.getSimpleName(), value));
  }

  /**
   * Returns the enum constant which has the specified value.
   *
   * @param value the string representation of the internal value
   * @return the enum constant
   * @throws IllegalArgumentException if the specified value is invalid
   */
  @Nonnull
  public static UserStatus of(@Nullable String value) {
    var s = StringUtils.strip(value);
    if (StringUtils.isEmpty(s)) {
      return TEMPORARY;
    }
    try {
      return of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("invalid value: %s(%s)".formatted(UserStatus.class.getSimpleName(), s), e);
    }
  }

  @Override
  @Nonnull
  public Integer value() {
    return value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
