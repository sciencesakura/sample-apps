package com.sciencesakura.sample.domain;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * An abstract query class.
 *
 * @param <Q> the type of the concrete subclass of this class
 */
@Getter
@Setter
public abstract class Query<Q extends Query<Q>> implements Cloneable, Serializable {

  @PositiveOrZero
  private int page;

  @Positive
  private int size;

  private List<@NotNull Order> order;

  @Nonnull
  public Pageable getPageRequest() {
    return PageRequest.of(page, size, order == null ? Sort.unsorted() : Sort.by(order));
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

  @Override
  @SuppressWarnings("unchecked")
  public Q clone() {
    try {
      return (Q) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError("MUST NOT BE REACHED", e);
    }
  }
}
