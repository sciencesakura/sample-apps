package com.sciencesakura.sample.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * An abstract entity class. The concrete subclass of this class must be annotated with
 * {@link jakarta.persistence.Entity}.
 *
 * @param <E> the type of the concrete subclass of this class
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class Entity<E extends Entity<E>> implements Cloneable, Serializable {

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

  @Override
  @SuppressWarnings("unchecked")
  public E clone() {
    try {
      return (E) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError("MUST NOT BE REACHED", e);
    }
  }
}
