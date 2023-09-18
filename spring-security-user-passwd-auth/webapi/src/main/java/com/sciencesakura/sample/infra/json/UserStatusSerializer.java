package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sciencesakura.sample.domain.user.UserStatus;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class UserStatusSerializer extends StdSerializer<UserStatus> {

  UserStatusSerializer() {
    super(UserStatus.class);
  }

  @Override
  public void serialize(UserStatus value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeNumber(value.value());
  }
}
