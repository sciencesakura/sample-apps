package com.sciencesakura.sample.infra.data;

import com.sciencesakura.sample.domain.ValueObject;
import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import org.springframework.core.convert.converter.Converter;

abstract class ValueObjectConverter<T extends ValueObject<T, V>, V extends Comparable<V>>
    implements AttributeConverter<T, V> {

  private final Converter<V, T> vtConverter;

  private final Converter<T, V> tvConverter;

  ValueObjectConverter(@Nonnull Converter<V, T> vtConverter, @Nonnull Converter<T, V> tvConverter) {
    this.vtConverter = vtConverter;
    this.tvConverter = tvConverter;
  }

  ValueObjectConverter(@Nonnull Converter<V, T> vtConverter) {
    this(vtConverter, ValueObject::value);
  }

  @Override
  public V convertToDatabaseColumn(T attribute) {
    return attribute == null ? null : tvConverter.convert(attribute);
  }

  @Override
  public T convertToEntityAttribute(V dbData) {
    return dbData == null ? null : vtConverter.convert(dbData);
  }
}
