package com.sciencesakura.sample.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sciencesakura.sample.api.model.Currency;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ItemObject
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ItemObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String code;

  private String name;

  private String description;

  private Double price;

  private Currency currency;

  private Double stock = 0.0d;

  public ItemObject() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ItemObject(String code, String name, Double price, Currency currency) {
    this.code = code;
    this.name = name;
    this.price = price;
    this.currency = currency;
  }

  public ItemObject code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  */
  @NotNull @Pattern(regexp = "[0-9A-Z]{8}") 
  @Schema(name = "code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ItemObject name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull @Size(max = 200) 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ItemObject description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @Size(max = 1000) 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ItemObject price(Double price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * minimum: 0.0
   * @return price
  */
  @NotNull @DecimalMin("0.0") 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public ItemObject currency(Currency currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
  */
  @NotNull @Valid 
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public ItemObject stock(Double stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Get stock
   * minimum: 0.0
   * @return stock
  */
  @DecimalMin("0.0") 
  @Schema(name = "stock", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stock")
  public Double getStock() {
    return stock;
  }

  public void setStock(Double stock) {
    this.stock = stock;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ItemObject itemObject = (ItemObject) o;
    return Objects.equals(this.code, itemObject.code) &&
        Objects.equals(this.name, itemObject.name) &&
        Objects.equals(this.description, itemObject.description) &&
        Objects.equals(this.price, itemObject.price) &&
        Objects.equals(this.currency, itemObject.currency) &&
        Objects.equals(this.stock, itemObject.stock);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, description, price, currency, stock);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ItemObject {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

