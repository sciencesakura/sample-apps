package com.sciencesakura.sample.domain.user;

import com.sciencesakura.sample.domain.ValueObject;
import jakarta.annotation.Nonnull;

/**
 * The value object which represents a password.
 */
public interface Password extends ValueObject<Password, String> {

  @Nonnull
  default String encodedValue() {
    return value();
  }
}
