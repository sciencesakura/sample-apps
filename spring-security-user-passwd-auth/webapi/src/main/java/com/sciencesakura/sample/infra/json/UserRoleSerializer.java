package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sciencesakura.sample.domain.user.UserRole;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class UserRoleSerializer extends StdSerializer<UserRole> {

  UserRoleSerializer() {
    super(UserRole.class);
  }

  @Override
  public void serialize(UserRole value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(value.getRole());
  }
}
