package com.sciencesakura.sample.domain;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;

/**
 * The entity which represents an item stock.
 */
@jakarta.persistence.Entity
@Getter
@Setter
public class ItemStock extends Entity<ItemStock> {

  @Id
  @OneToOne
  @JoinColumn(name = "item_id")
  @ToStringExclude
  private Item item;

  private BigDecimal quantity;

  @Version
  private long version;

  protected ItemStock() {
  }

  /**
   * Constructs an item stock.
   */
  public ItemStock(BigDecimal quantity) {
    this.quantity = quantity;
  }

  @Override
  public int hashCode() {
    return Objects.hash(item == null ? null : item.getId(), quantity, version);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ItemStock s) {
      if (item == null) {
        return s.item == null && Objects.equals(s.quantity, quantity) && s.version == version;
      } else {
        return s.item != null && Objects.equals(s.item.getId(), item.getId())
            && Objects.equals(s.quantity, quantity) && s.version == version;
      }
    }
    return false;
  }
}
