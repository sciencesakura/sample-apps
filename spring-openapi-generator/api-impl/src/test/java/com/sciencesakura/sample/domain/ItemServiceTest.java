package com.sciencesakura.sample.domain;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.db.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.Destination;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemServiceTest {

  static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

  @Autowired
  ItemService itemService;

  @Autowired
  Destination destination;

  @Autowired
  DataSource dataSource;

  @BeforeEach
  void setUp() {
    var dbSetup = new DbSetup(destination, sequenceOf(
        deleteAllFrom("item"),
        insertInto("item")
            .columns("id", "code", "name", "description", "price", "currency")
            .values("00000000-0000-0000-0000-000000000001", "00000001", "Item 1", "Desc 1", 100, "JPY")
            .values("00000000-0000-0000-0000-000000000002", "00000002", "Item 2", null, 200, "JPY")
            .build(),
        insertInto("item_stock")
            .columns("item_id", "quantity")
            .values("00000000-0000-0000-0000-000000000001", 10)
            .build()
    ));
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @Nested
  class findByCode {

    @AfterEach
    void tearDown() {
      dbSetupTracker.skipNextLaunch();
    }

    @Test
    void return_item() {
      var code = "00000001";
      var actual = itemService.findByCode(code);
      assertThat(actual).hasValueSatisfying(i -> {
        assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertThat(i.getCode()).isEqualTo(code);
        assertThat(i.getName()).isEqualTo("Item 1");
        assertThat(i.getDescription()).isEqualTo("Desc 1");
        assertThat(i.getPrice()).isEqualByComparingTo("100");
        assertThat(i.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
        assertThat(i.getStock()).satisfies(s -> {
          assertThat(s.getItem()).isSameAs(i);
          assertThat(s.getQuantity()).isEqualByComparingTo("10");
        });
      });
    }

    @Test
    void return_item_that_has_no_optional_props() {
      var code = "00000002";
      var actual = itemService.findByCode(code);
      assertThat(actual).hasValueSatisfying(i -> {
        assertThat(i.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertThat(i.getCode()).isEqualTo(code);
        assertThat(i.getName()).isEqualTo("Item 2");
        assertThat(i.getDescription()).isNull();
        assertThat(i.getPrice()).isEqualByComparingTo("200");
        assertThat(i.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
        assertThat(i.getStock()).isNull();
      });
    }

    @Test
    void return_empty_if_not_found() {
      var code = "99999999";
      var actual = itemService.findByCode(code);
      assertThat(actual).isEmpty();
    }
  }

  @Nested
  class save {

    Changes changes;

    @BeforeEach
    void setUp() {
      changes = new Changes(dataSource);
    }

    @Test
    void add_item() {
      changes.setStartPointNow();
      var item = new Item(
          "00000003",
          "Item 3",
          "Desc 3",
          BigDecimal.valueOf(300),
          Currency.getInstance("JPY")
      );
      item.setStock(new ItemStock(BigDecimal.valueOf(30)));
      var actual = itemService.save(item);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getCode()).isEqualTo("00000003");
      assertThat(actual.getName()).isEqualTo("Item 3");
      assertThat(actual.getDescription()).isEqualTo("Desc 3");
      assertThat(actual.getPrice()).isEqualByComparingTo("300");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
      assertThat(actual.getStock()).satisfies(s -> {
        assertThat(s.getItem()).isSameAs(actual);
        assertThat(s.getQuantity()).isEqualByComparingTo("30");
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(2)
          .changeOfCreationOnTable("item")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfCreationOnTable("item_stock")
          .rowAtEndPoint()
          .value("item_id").isEqualTo(actual.getId());
    }

    @Test
    void add_item_that_has_no_optional_props() {
      changes.setStartPointNow();
      var item = new Item(
          "00000003",
          "Item 3",
          null,
          BigDecimal.valueOf(300),
          Currency.getInstance("JPY")
      );
      var actual = itemService.save(item);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getCode()).isEqualTo("00000003");
      assertThat(actual.getName()).isEqualTo("Item 3");
      assertThat(actual.getDescription()).isNull();
      assertThat(actual.getPrice()).isEqualByComparingTo("300");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
      assertThat(actual.getStock()).isNull();
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfCreationOnTable("item")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId());
    }

    @Test
    void modify_item() {
      changes.setStartPointNow();
      var item = new Item(
          "00000001",
          "Item 1'",
          "Desc 1'",
          BigDecimal.valueOf(101),
          Currency.getInstance("USD")
      );
      item.setStock(new ItemStock(BigDecimal.valueOf(11)));
      var actual = itemService.save(item);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getCode()).isEqualTo("00000001");
      assertThat(actual.getName()).isEqualTo("Item 1'");
      assertThat(actual.getDescription()).isEqualTo("Desc 1'");
      assertThat(actual.getPrice()).isEqualByComparingTo("101");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("USD"));
      assertThat(actual.getStock()).satisfies(s -> {
        assertThat(s.getItem()).isSameAs(actual);
        assertThat(s.getQuantity()).isEqualByComparingTo("11");
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(2)
          .changeOfModificationOnTable("item")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfModificationOnTable("item_stock")
          .rowAtEndPoint()
          .value("item_id").isEqualTo(actual.getId());
    }

    @Test
    void modify_item_to_add_stock() {
      changes.setStartPointNow();
      var item = new Item(
          "00000002",
          "Item 2'",
          "Desc 2'",
          BigDecimal.valueOf(202),
          Currency.getInstance("USD")
      );
      item.setStock(new ItemStock(BigDecimal.valueOf(22)));
      var actual = itemService.save(item);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getCode()).isEqualTo("00000002");
      assertThat(actual.getName()).isEqualTo("Item 2'");
      assertThat(actual.getDescription()).isEqualTo("Desc 2'");
      assertThat(actual.getPrice()).isEqualByComparingTo("202");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("USD"));
      assertThat(actual.getStock()).satisfies(s -> {
        assertThat(s.getItem()).isSameAs(actual);
        assertThat(s.getQuantity()).isEqualByComparingTo("22");
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(2)
          .changeOfModificationOnTable("item")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfCreationOnTable("item_stock")
          .rowAtEndPoint()
          .value("item_id").isEqualTo(actual.getId());
    }

    @Test
    void modify_item_to_remove_stock() {
      changes.setStartPointNow();
      var item = new Item(
          "00000001",
          "Item 1'",
          "Desc 1'",
          BigDecimal.valueOf(101),
          Currency.getInstance("USD")
      );
      var actual = itemService.save(item);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getCode()).isEqualTo("00000001");
      assertThat(actual.getName()).isEqualTo("Item 1'");
      assertThat(actual.getDescription()).isEqualTo("Desc 1'");
      assertThat(actual.getPrice()).isEqualByComparingTo("101");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("USD"));
      assertThat(actual.getStock()).isNull();
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(2)
          .changeOfModificationOnTable("item")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfDeletionOnTable("item_stock")
          .rowAtStartPoint()
          .value("item_id").isEqualTo(actual.getId());
    }
  }

  @Nested
  class delete {

    Changes changes;

    @BeforeEach
    void setUp() {
      changes = new Changes(dataSource);
    }

    @Test
    void delete_item() {
      changes.setStartPointNow();
      var code = "00000001";
      itemService.delete(code);
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(2)
          .changeOfDeletionOnTable("item")
          .rowAtStartPoint()
          .value("code").isEqualTo(code)
          .changeOfDeletionOnTable("item_stock")
          .rowAtStartPoint()
          .value("item_id").isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    void does_not_throw_exception_if_not_found() {
      changes.setStartPointNow();
      var code = "99999999";
      assertThatNoException().isThrownBy(() -> itemService.delete(code));
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }
  }

  @Nested
  class addStockQuantity {

    Changes changes;

    @BeforeEach
    void setUp() {
      changes = new Changes(dataSource);
    }

    @Test
    void add_quantity_to_existing_stock() {
      changes.setStartPointNow();
      var code = "00000001";
      var actual = itemService.addStockQuantity(code, BigDecimal.valueOf(1));
      assertThat(actual.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      assertThat(actual.getCode()).isEqualTo(code);
      assertThat(actual.getName()).isEqualTo("Item 1");
      assertThat(actual.getDescription()).isEqualTo("Desc 1");
      assertThat(actual.getPrice()).isEqualByComparingTo("100");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
      assertThat(actual.getStock()).satisfies(s -> {
        assertThat(s.getItem()).isSameAs(actual);
        assertThat(s.getQuantity()).isEqualByComparingTo("11");
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfModificationOnTable("item_stock")
          .rowAtEndPoint()
          .value("item_id").isEqualTo(actual.getId());
    }

    @Test
    void add_quantity_to_non_existing_stock() {
      changes.setStartPointNow();
      var code = "00000002";
      var actual = itemService.addStockQuantity(code, BigDecimal.valueOf(1));
      assertThat(actual.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"));
      assertThat(actual.getCode()).isEqualTo(code);
      assertThat(actual.getName()).isEqualTo("Item 2");
      assertThat(actual.getDescription()).isNull();
      assertThat(actual.getPrice()).isEqualByComparingTo("200");
      assertThat(actual.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
      assertThat(actual.getStock()).satisfies(s -> {
        assertThat(s.getItem()).isSameAs(actual);
        assertThat(s.getQuantity()).isEqualByComparingTo("1");
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfCreationOnTable("item_stock")
          .rowAtEndPoint()
          .value("item_id").isEqualTo(actual.getId());
    }

    @Test
    void does_not_throw_exception_if_stock_is_zero() {
      var code = "00000001";
      assertThatNoException().isThrownBy(() -> itemService.addStockQuantity(code, BigDecimal.valueOf(-10)));
    }

    @Test
    void throw_exception_if_stock_is_negative() {
      changes.setStartPointNow();
      var code = "00000001";
      assertThatThrownBy(() -> itemService.addStockQuantity(code, BigDecimal.valueOf(-11)))
          .isInstanceOf(IllegalArgumentException.class);
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }

    @Test
    void throw_exception_if_not_found() {
      changes.setStartPointNow();
      var code = "99999999";
      assertThatThrownBy(() -> itemService.addStockQuantity(code, BigDecimal.valueOf(1)))
          .isInstanceOf(NotFoundException.class);
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }
  }
}
