package com.sciencesakura.sample.infra.data;

import com.sciencesakura.sample.domain.user.UserStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class UserStatusConverter extends ValueObjectConverter<UserStatus, Integer> {

  UserStatusConverter() {
    super(UserStatus::of);
  }
}
