package com.sciencesakura.sample.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * The entity which represents an item.
 */
@jakarta.persistence.Entity
@Getter
@Setter
public class Item extends Entity<Item> {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(unique = true, updatable = false)
  private String code;

  private String name;

  private String description;

  private BigDecimal price;

  private Currency currency;

  @Version
  private long version;

  @OneToOne(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private ItemStock stock;

  protected Item() {
  }

  /**
   * Constructs an item.
   */
  public Item(String code, String name, String description, BigDecimal price, Currency currency) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.price = price;
    this.currency = currency;
  }

  /**
   * Set th stock.
   *
   * @param stock the stock
   */
  public void setStock(@Nullable ItemStock stock) {
    if (stock != null) {
      stock.setItem(this);
    }
    this.stock = stock;
  }
}
