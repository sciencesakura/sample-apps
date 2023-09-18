package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sciencesakura.sample.domain.user.UserStatus;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
class UserStatusDeserializer extends StdDeserializer<UserStatus> {

  UserStatusDeserializer() {
    super(UserStatus.class);
  }

  @Override
  public UserStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return switch (p.currentToken()) {
      case VALUE_NUMBER_INT -> UserStatus.of(p.getIntValue());
      case VALUE_STRING -> UserStatus.of(p.getText());
      default -> throw MismatchedInputException.from(p, handledType(), "must be integer");
    };
  }
}
