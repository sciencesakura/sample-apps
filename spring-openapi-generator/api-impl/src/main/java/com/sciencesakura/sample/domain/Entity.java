package com.sciencesakura.sample.domain;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An abstract entity class. The concrete subclass of this class MUST be annotated with
 * {@link jakarta.persistence.Entity}.
 *
 * @param <E> the type of the concrete subclass
 */
abstract class Entity<E extends Entity<E>> implements Cloneable, Serializable {

  @Override
  @SuppressWarnings("unchecked")
  public E clone() {
    try {
      return (E) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError("MUST NOT BE REACHED", e);
    }
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
