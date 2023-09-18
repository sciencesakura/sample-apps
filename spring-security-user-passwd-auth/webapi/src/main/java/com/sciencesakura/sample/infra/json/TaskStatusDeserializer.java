package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sciencesakura.sample.domain.task.TaskStatus;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class TaskStatusDeserializer extends StdDeserializer<TaskStatus> {

  TaskStatusDeserializer() {
    super(TaskStatus.class);
  }

  @Override
  public TaskStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return switch (p.currentToken()) {
      case VALUE_NUMBER_INT -> TaskStatus.of(p.getIntValue());
      case VALUE_STRING -> TaskStatus.of(p.getText());
      default -> throw MismatchedInputException.from(p, handledType(), "must be integer");
    };
  }
}
