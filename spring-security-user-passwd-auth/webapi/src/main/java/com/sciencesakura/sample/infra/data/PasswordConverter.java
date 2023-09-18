package com.sciencesakura.sample.infra.data;

import com.sciencesakura.sample.domain.user.Password;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

@Converter(autoApply = true)
class PasswordConverter extends ValueObjectConverter<Password, String> {

  PasswordConverter() {
    super(PersistedPassword::of, Password::encodedValue);
  }

  private record PersistedPassword(String value) implements Password {

    private static Password of(String value) {
      var s = StringUtils.strip(value);
      return StringUtils.isEmpty(s) ? null : new PersistedPassword(s);
    }

    @Override
    public String toString() {
      return "******";
    }
  }
}
