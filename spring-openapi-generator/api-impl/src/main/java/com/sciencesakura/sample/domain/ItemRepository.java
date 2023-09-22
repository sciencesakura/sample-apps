package com.sciencesakura.sample.domain;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The repository in which items are stored.
 */
interface ItemRepository extends JpaRepository<Item, UUID>, JpaSpecificationExecutor<Item> {

  /**
   * Retrieve an item by its code.
   *
   * @param code the code
   * @return the item with the given code, or empty if not found
   */
  @Nonnull
  Optional<Item> findByCode(@Nonnull String code);

  /**
   * Delete an item by its code.
   *
   * @param code the code
   */
  void deleteByCode(@Nonnull String code);
}
