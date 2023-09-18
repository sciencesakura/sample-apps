package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sciencesakura.sample.domain.task.Priority;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class PriorityDeserializer extends StdDeserializer<Priority> {

  PriorityDeserializer() {
    super(Priority.class);
  }

  @Override
  public Priority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return switch (p.currentToken()) {
      case VALUE_NUMBER_INT -> Priority.of(p.getIntValue());
      case VALUE_STRING -> Priority.of(p.getText());
      default -> throw MismatchedInputException.from(p, handledType(), "must be integer");
    };
  }
}
