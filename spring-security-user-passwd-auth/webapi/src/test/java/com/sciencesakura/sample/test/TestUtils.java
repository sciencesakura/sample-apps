package com.sciencesakura.sample.test;

import jakarta.annotation.Nonnull;
import org.assertj.core.api.Condition;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class TestUtils {

  private TestUtils() {
  }

  @Nonnull
  public static String dir(@Nonnull Class<?> clazz) {
    return clazz.getName().replace('.', '/');
  }

  @Nonnull
  public static Condition<String> matchedPassword(@Nonnull String rawPassword, @Nonnull PasswordEncoder encoder) {
    return new Condition<>(s -> encoder.matches(rawPassword, s), "\"%s\" (raw)", rawPassword);
  }
}
