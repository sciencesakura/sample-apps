package com.sciencesakura.sample.domain;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.Destination;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
class ItemServiceFindAllTest {

  static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

  @Autowired
  ItemService itemService;

  @Autowired
  Destination destination;

  @BeforeEach
  void setUp() {
    var dbSetup = new DbSetup(destination, sequenceOf(
        deleteAllFrom("item"),
        insertInto("item")
            .columns("id", "code", "name", "description", "price", "currency")
            .values("00000000-0000-0000-0000-000000000001", "XXXXXXX1", "XXXXX", "XXXXX", 100, "JPY")
            .values("00000000-0000-0000-0000-000000000002", "XAAAAAX2", "XXXXX", "XXXXX", 100, "JPY")
            .values("00000000-0000-0000-0000-000000000003", "XXXXXXX3", "XAAAX", "XXXXX", 100, "JPY")
            .values("00000000-0000-0000-0000-000000000004", "XXXXXXX4", "XXXXX", "XAAAX", 100, "JPY")
            .build(),
        insertInto("item_stock")
            .columns("item_id", "quantity")
            .values("00000000-0000-0000-0000-000000000002", 10)
            .values("00000000-0000-0000-0000-000000000003", 10)
            .values("00000000-0000-0000-0000-000000000004", 10)
            .build()
    ));
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @AfterEach
  void tearDown() {
    dbSetupTracker.skipNextLaunch();
  }

  @Test
  void with_searchText() {
    var pageable = PageRequest.of(0, 10, Sort.by("id"));
    var actual = itemService.findAll("AAA", pageable);
    assertThat(actual).satisfiesExactly(
        i -> {
          assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"));
          assertThat(i.getCode()).isEqualTo("XAAAAAX2");
          assertThat(i.getName()).isEqualTo("XXXXX");
          assertThat(i.getDescription()).isEqualTo("XXXXX");
          assertThat(i.getPrice()).isEqualByComparingTo("100");
          assertThat(i.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
          assertThat(i.getStock()).satisfies(s -> {
            assertThat(s.getItem()).isSameAs(i);
            assertThat(s.getQuantity()).isEqualByComparingTo("10");
          });
        },
        i -> assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000003")),
        i -> assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000004"))
    );
  }

  @Test
  void without_searchText() {
    var pageable = PageRequest.of(0, 10, Sort.by("id"));
    var actual = itemService.findAll(null, pageable);
    assertThat(actual).satisfiesExactly(
        i -> assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001")),
        i -> assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")),
        i -> assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000003")),
        i -> assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000004"))
    );
  }
}
