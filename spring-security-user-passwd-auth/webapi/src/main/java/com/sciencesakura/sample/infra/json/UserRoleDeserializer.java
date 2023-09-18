package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sciencesakura.sample.domain.user.UserRole;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class UserRoleDeserializer extends StdDeserializer<UserRole> {

  UserRoleDeserializer() {
    super(UserRole.class);
  }

  @Override
  public UserRole deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.currentToken() != JsonToken.VALUE_STRING) {
      throw MismatchedInputException.from(p, handledType(), "must be string");
    }
    var s = p.getText().strip();
    return s.isEmpty() ? null : new UserRole(s);
  }
}
