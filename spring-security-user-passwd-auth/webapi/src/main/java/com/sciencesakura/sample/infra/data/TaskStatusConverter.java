package com.sciencesakura.sample.infra.data;

import com.sciencesakura.sample.domain.task.TaskStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class TaskStatusConverter extends ValueObjectConverter<TaskStatus, Integer> {

  TaskStatusConverter() {
    super(TaskStatus::of);
  }
}
