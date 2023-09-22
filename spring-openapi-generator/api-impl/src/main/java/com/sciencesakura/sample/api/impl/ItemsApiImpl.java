package com.sciencesakura.sample.api.impl;

import com.sciencesakura.sample.api.ItemsApi;
import com.sciencesakura.sample.api.model.Currency;
import com.sciencesakura.sample.api.model.ItemObject;
import com.sciencesakura.sample.api.model.UpdateItemStockRequest;
import com.sciencesakura.sample.domain.Item;
import com.sciencesakura.sample.domain.ItemService;
import com.sciencesakura.sample.domain.ItemStock;
import com.sciencesakura.sample.domain.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
class ItemsApiImpl implements ItemsApi {

  private final ItemService itemService;

  @Override
  public ResponseEntity<ItemObject> getItem(String code) {
    return itemService.findByCode(code)
        .map(this::convert)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("Item", code));
  }

  @Override
  public ResponseEntity<List<ItemObject>> getItemList(String q, Integer p, Integer s) {
    var items = itemService.findAll(q, PageRequest.of(p, s, Sort.by("code")));
    return ResponseEntity.ok(items.map(this::convert).toList());
  }

  @Override
  public ResponseEntity<Void> createItem(ItemObject itemObject) {
    var created = itemService.save(convert(itemObject));
    var location = UriComponentsBuilder.fromPath("/items/{code}")
        .build(created.getCode());
    return ResponseEntity.created(location).build();
  }

  @Override
  public ResponseEntity<Void> updateItem(String code, ItemObject itemObject) {
    var item = convert(itemObject);
    item.setCode(code);
    itemService.save(item);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteItem(String code) {
    itemService.delete(code);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> updateItemStockQuantity(String code, UpdateItemStockRequest updateItemStockRequest) {
    itemService.addStockQuantity(code, BigDecimal.valueOf(updateItemStockRequest.getQuantity()));
    return ResponseEntity.noContent().build();
  }

  private Item convert(ItemObject itemObject) {
    var item = new Item(
        itemObject.getCode(),
        itemObject.getName(),
        itemObject.getDescription(),
        BigDecimal.valueOf(itemObject.getPrice()),
        java.util.Currency.getInstance(itemObject.getCurrency().getValue())
    );
    item.setStock(new ItemStock(BigDecimal.valueOf(itemObject.getStock())));
    return item;
  }

  private ItemObject convert(Item item) {
    var object = new ItemObject();
    object.setCode(item.getCode());
    object.setName(item.getName());
    object.setDescription(item.getDescription());
    object.setPrice(item.getPrice().doubleValue());
    object.setCurrency(Currency.fromValue(item.getCurrency().getCurrencyCode()));
    if (item.getStock() == null) {
      object.setStock(0.0);
    } else {
      object.setStock(item.getStock().getQuantity().doubleValue());
    }
    return object;
  }
}
