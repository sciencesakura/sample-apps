package com.sciencesakura.sample.domain;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Thrown when a business rule is violated.
 */
public abstract class BusinessException extends RuntimeException {

  protected BusinessException(@Nonnull String message, Throwable cause) {
    super(message, cause);
  }

  protected BusinessException(@Nonnull String message) {
    super(message);
  }

  /**
   * Returns the details of this exception.
   *
   * @return the details of this exception
   */
  @Nullable
  public abstract Object getDetails();
}
