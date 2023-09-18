package com.sciencesakura.sample.domain;

import jakarta.annotation.Nonnull;
import java.io.Serializable;

/**
 * An immutable and comparable value object.
 *
 * @param <V> the type of the internal value
 */
public interface ValueObject<T extends ValueObject<T, V>, V extends Comparable<V>> extends Comparable<T>, Serializable {

  /**
   * Returns the internal value.
   *
   * @return the internal value
   */
  @Nonnull
  V value();

  @Override
  default int compareTo(T o) {
    return value().compareTo(o.value());
  }
}
