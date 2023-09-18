package com.sciencesakura.sample.infra.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class PrincipalAuditorAwareTest {

  @Autowired
  PrincipalAuditorAware auditorAware;

  @Nested
  class getCurrentAuditor {

    @Test
    @WithMockUser("mock user")
    void return_current_user_if_authenticated() {
      var actual = auditorAware.getCurrentAuditor();
      assertThat(actual).contains("mock user");
    }

    @Test
    void return_anonymous_if_not_authenticated() {
      var actual = auditorAware.getCurrentAuditor();
      assertThat(actual).contains("anonymous");
    }
  }
}
