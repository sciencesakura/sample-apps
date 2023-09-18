package com.sciencesakura.sample.domain;

import com.sciencesakura.sample.util.Messages;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Thrown when the requested resource is not found unexpectedly.
 */
@Getter
public class NotFoundException extends BusinessException {

  private final Details details;

  public NotFoundException(@Nonnull String resource, Object identifier, Throwable cause) {
    super(buildMessage(resource, identifier), cause);
    this.details = new Details(resource, identifier);
  }

  public NotFoundException(@Nonnull String resource, Object identifier) {
    super(buildMessage(resource, identifier));
    this.details = new Details(resource, identifier);
  }

  @Nonnull
  public String getResource() {
    return details.resource;
  }

  @Nullable
  public Object getIdentifier() {
    return details.identifier;
  }

  private static String buildMessage(String resource, Object identifier) {
    return Messages.get("NotFoundException.message", resource, identifier);
  }

  private record Details(String resource, Object identifier) implements Serializable {

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
  }
}
