package com.sciencesakura.sample.domain;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;

/**
 * Thrown when the specified resource is not found.
 */
@Getter
public class NotFoundException extends RuntimeException {

  private final Details details;

  public NotFoundException(@Nonnull String resource, @Nullable Object identifier) {
    super(String.format("%s not found: %s", resource, identifier));
    this.details = new Details(resource, identifier);
  }

  /**
   * The details of the not found resource.
   *
   * @param resource   the resource name
   * @param identifier the identifier of the resource
   */
  public record Details(String resource, Object identifier) {

  }
}
