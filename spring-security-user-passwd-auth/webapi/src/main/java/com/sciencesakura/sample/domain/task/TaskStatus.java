package com.sciencesakura.sample.domain.task;

import com.sciencesakura.sample.domain.ValueObject;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * The enumeration type which represents a task status.
 */
@RequiredArgsConstructor
public enum TaskStatus implements ValueObject<TaskStatus, Integer> {

  CREATED(0),

  WORK_IN_PROGRESS(1),

  COMPLETED(90);

  private final int value;

  /**
   * Returns the enum constant which has the specified value.
   *
   * @param value the internal value
   * @return the enum constant
   * @throws IllegalArgumentException if the specified value is invalid
   */
  @Nonnull
  public static TaskStatus of(int value) {
    for (var constant : values()) {
      if (constant.value == value) {
        return constant;
      }
    }
    throw new IllegalArgumentException("invalid value: %s(%d)".formatted(TaskStatus.class.getSimpleName(), value));
  }

  /**
   * Returns the enum constant which has the specified value.
   *
   * @param value the string representation of the internal value
   * @return the enum constant
   * @throws IllegalArgumentException if the specified value is invalid
   */
  @Nonnull
  public static TaskStatus of(@Nullable String value) {
    var s = StringUtils.strip(value);
    if (StringUtils.isEmpty(s)) {
      return CREATED;
    }
    try {
      return of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("invalid value: %s(%s)".formatted(TaskStatus.class.getSimpleName(), s), e);
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
