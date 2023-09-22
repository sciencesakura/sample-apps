package com.sciencesakura.sample.api;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sciencesakura.sample.domain.Item;
import com.sciencesakura.sample.domain.ItemService;
import com.sciencesakura.sample.domain.ItemStock;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ItemsApiTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ItemService itemService;

  @Nested
  class getItem {

    @Test
    void return_200() throws Exception {
      var code = "00000001";
      var stubReturn = dummyItem();
      when(itemService.findByCode(eq(code))).thenReturn(Optional.of(stubReturn));
      mockMvc.perform(get("/items/{code}", code))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              {
                "code": "00000001",
                "name": "Item 1",
                "description": "Desc 1",
                "price": 100,
                "currency": "JPY",
                "stock": 10
              }
              """));
    }

    @Test
    void return_200_without_optional_props() throws Exception {
      var code = "00000001";
      var stubReturn = dummyItem();
      stubReturn.setDescription(null);
      stubReturn.setStock(null);
      when(itemService.findByCode(eq(code))).thenReturn(Optional.of(stubReturn));
      mockMvc.perform(get("/items/{code}", code))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              {
                "code": "00000001",
                "name": "Item 1",
                "price": 100,
                "currency": "JPY"
              }
              """));
    }

    @Test
    void return_404_when_item_not_found() throws Exception {
      var code = "00000001";
      when(itemService.findByCode(eq(code))).thenReturn(Optional.empty());
      mockMvc.perform(get("/items/{code}", code))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              {
                "message": "Item not found: 00000001",
                "details": {
                  "resource": "Item",
                  "identifier": "00000001"
                }
              }
              """));
    }
  }

  @Nested
  class getItemList {

    @Test
    void return_200() throws Exception {
      var stubReturn = List.of(dummyItem());
      var stubArg = PageRequest.of(3, 30, Sort.by("code"));
      when(itemService.findAll(eq("text"), eq(stubArg))).thenReturn(new PageImpl<>(stubReturn));
      mockMvc.perform(get("/items")
              .param("q", "text")
              .param("p", "3")
              .param("s", "30"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              [
                {
                  "code": "00000001",
                  "name": "Item 1",
                  "description": "Desc 1",
                  "price": 100,
                  "currency": "JPY",
                  "stock": 10
                }
              ]
              """));
    }

    @Test
    void return_200_without_param() throws Exception {
      var stubReturn = List.of(dummyItem());
      var stubArg = PageRequest.of(0, 20, Sort.by("code"));
      when(itemService.findAll(isNull(), eq(stubArg))).thenReturn(new PageImpl<>(stubReturn));
      mockMvc.perform(get("/items"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              [
                {
                  "code": "00000001",
                  "name": "Item 1",
                  "description": "Desc 1",
                  "price": 100,
                  "currency": "JPY",
                  "stock": 10
                }
              ]
              """));
    }
  }

  @Nested
  class createItem {

    @Test
    void return_201() throws Exception {
      var stubArg = dummyItem();
      var stubReturn = stubArg.clone();
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      stubReturn.setStock(stubReturn.getStock().clone());
      when(itemService.save(eq(stubArg))).thenReturn(stubReturn);
      mockMvc.perform(post("/items")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "code": "00000001",
                    "name": "Item 1",
                    "description": "Desc 1",
                    "price": 100,
                    "currency": "JPY",
                    "stock": 10
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", endsWith("/items/00000001")));
    }

    @Test
    void return_201_without_optional_props() throws Exception {
      var stubArg = dummyItem();
      stubArg.setDescription(null);
      stubArg.getStock().setQuantity(BigDecimal.valueOf(0.0));
      var stubReturn = stubArg.clone();
      stubReturn.setStock(stubReturn.getStock().clone());
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      when(itemService.save(eq(stubArg))).thenReturn(stubReturn);
      mockMvc.perform(post("/items")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "code": "00000001",
                    "name": "Item 1",
                    "price": 100,
                    "currency": "JPY"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", endsWith("/items/00000001")));
    }
  }

  @Nested
  class updateItem {

    @Test
    void return_204() throws Exception {
      var code = "00000001";
      var stubArg = dummyItem();
      mockMvc.perform(put("/items/{code}", code)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "code": "00000001",
                    "name": "Item 1",
                    "description": "Desc 1",
                    "price": 100,
                    "currency": "JPY",
                    "stock": 10
                  }
                  """))
          .andExpect(status().isNoContent());
      verify(itemService).save(stubArg);
    }

    @Test
    void return_204_without_optional_props() throws Exception {
      var code = "00000001";
      var stubArg = dummyItem();
      stubArg.setDescription(null);
      stubArg.getStock().setQuantity(BigDecimal.valueOf(0.0));
      mockMvc.perform(put("/items/{code}", code)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "code": "00000001",
                    "name": "Item 1",
                    "price": 100,
                    "currency": "JPY"
                  }
                  """))
          .andExpect(status().isNoContent());
      verify(itemService).save(stubArg);
    }
  }

  @Nested
  class deleteItem {

    @Test
    void return_204() throws Exception {
      var code = "00000001";
      mockMvc.perform(delete("/items/{code}", code))
          .andExpect(status().isNoContent());
      verify(itemService).delete(code);
    }
  }

  @Nested
  class updateItemStockQuantity {

    @Test
    void return_204() throws Exception {
      var code = "00000001";
      var stubArg = BigDecimal.valueOf(10.0);
      mockMvc.perform(put("/items/{code}/stock", code)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "quantity": 10
                  }
                  """))
          .andExpect(status().isNoContent());
      verify(itemService).addStockQuantity(code, stubArg);
    }
  }

  private Item dummyItem() {
    var item = new Item(
        "00000001",
        "Item 1",
        "Desc 1",
        BigDecimal.valueOf(100.0),
        Currency.getInstance("JPY")
    );
    item.setStock(new ItemStock(BigDecimal.valueOf(10.0)));
    return item;
  }
}
