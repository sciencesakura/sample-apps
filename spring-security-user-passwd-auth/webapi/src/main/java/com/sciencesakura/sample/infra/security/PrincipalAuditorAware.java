package com.sciencesakura.sample.infra.security;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
class PrincipalAuditorAware implements AuditorAware<String> {

  private static final Optional<String> defaultAuditor = Optional.of("anonymous");

  @Override
  @Nonnull
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .filter(UserDetails.class::isInstance)
        .map(p -> ((UserDetails) p).getUsername())
        .or(() -> defaultAuditor);
  }
}
