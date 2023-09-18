package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sciencesakura.sample.domain.task.Priority;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class PrioritySerializer extends StdSerializer<Priority> {

  PrioritySerializer() {
    super(Priority.class);
  }

  @Override
  public void serialize(Priority value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeNumber(value.value());
  }
}
