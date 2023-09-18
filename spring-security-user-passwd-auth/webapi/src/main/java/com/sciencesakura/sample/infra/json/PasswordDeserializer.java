package com.sciencesakura.sample.infra.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sciencesakura.sample.domain.user.Password;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

@JsonComponent
class PasswordDeserializer extends StdDeserializer<Password> {

  private final PasswordEncoder encoder;

  PasswordDeserializer(PasswordEncoder encoder) {
    super(Password.class);
    this.encoder = encoder;
  }

  @Override
  public Password deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.currentToken() != JsonToken.VALUE_STRING) {
      throw MismatchedInputException.from(p, handledType(), "must be string");
    }
    var s = p.getText().strip();
    return s.isEmpty() ? null : new DeserializedPassword(s, encoder.encode(s));
  }

  private record DeserializedPassword(String value, String encodedValue) implements Password {

    @Override
    public String toString() {
      return "******";
    }
  }
}
