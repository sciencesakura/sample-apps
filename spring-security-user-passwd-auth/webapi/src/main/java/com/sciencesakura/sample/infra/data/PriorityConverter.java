package com.sciencesakura.sample.infra.data;

import com.sciencesakura.sample.domain.task.Priority;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class PriorityConverter extends ValueObjectConverter<Priority, Integer> {

  PriorityConverter() {
    super(Priority::of);
  }
}
