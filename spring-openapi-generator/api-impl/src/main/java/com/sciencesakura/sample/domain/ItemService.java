package com.sciencesakura.sample.domain;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service class which provides operations on items.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  /**
   * Retrieve all items by the given search text.
   *
   * @param searchText the search text
   * @param pageable   the pageable
   * @return the page of items
   */
  @Transactional(readOnly = true)
  @Nonnull
  public Page<Item> findAll(@Nullable String searchText, @Nonnull Pageable pageable) {
    var spec = new ArrayList<Specification<Item>>();
    if (StringUtils.isNotEmpty(searchText)) {
      var pattern = '%' + searchText + '%';
      spec.add((root, q, cb) -> cb.or(
          cb.like(root.get(Item_.code), pattern),
          cb.like(root.get(Item_.name), pattern),
          cb.like(root.get(Item_.description), pattern)
      ));
    }
    return itemRepository.findAll(Specification.allOf(spec), pageable);
  }

  /**
   * Retrieve an item by its code.
   *
   * @param code the code
   * @return the item with the given code, or empty if not found
   */
  @Transactional(readOnly = true)
  @Nonnull
  public Optional<Item> findByCode(@Nonnull String code) {
    return itemRepository.findByCode(code);
  }

  /**
   * Persist an item.
   *
   * @param newItem the item to persist
   * @return the persisted item
   */
  @Nonnull
  public Item save(@Nonnull Item newItem) {
    return findByCode(newItem.getCode())
        .map(i -> update(i, newItem))
        .orElseGet(() -> itemRepository.save(newItem));
  }

  /**
   * Update the stock of an item by its code.
   *
   * @param code     the code
   * @param quantity the quantity to add
   * @return the updated item
   */
  @Nonnull
  public Item addStockQuantity(@Nonnull String code, @Nonnull BigDecimal quantity) {
    return findByCode(code)
        .map(i -> addQuantity(i, quantity))
        .orElseThrow(() -> new NotFoundException("Item", code));
  }

  /**
   * Delete an item by its code.
   *
   * @param code the code
   */
  public void delete(@Nonnull String code) {
    itemRepository.deleteByCode(code);
  }

  private Item update(Item current, Item newItem) {
    current.setName(newItem.getName());
    current.setDescription(newItem.getDescription());
    current.setPrice(newItem.getPrice());
    current.setCurrency(newItem.getCurrency());
    if (current.getStock() == null || newItem.getStock() == null) {
      current.setStock(newItem.getStock());
    } else {
      current.getStock().setQuantity(newItem.getStock().getQuantity());
    }
    return current;
  }

  private Item addQuantity(Item current, BigDecimal quantity) {
    if (current.getStock() == null) {
      current.setStock(new ItemStock(quantity));
    } else {
      current.getStock().setQuantity(current.getStock().getQuantity().add(quantity));
    }
    if (current.getStock().getQuantity().signum() < 0) {
      throw new IllegalArgumentException("Stock quantity must not be negative");
    }
    return current;
  }
}
