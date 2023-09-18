package com.sciencesakura.sample.infra.security;

import com.sciencesakura.sample.domain.user.UserInfo;
import com.sciencesakura.sample.domain.user.UserRole;
import com.sciencesakura.sample.domain.user.UserService;
import com.sciencesakura.sample.domain.user.UserStatus;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserDetailsServiceImpl implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) {
    return userService.findByEmailAddress(username)
        .map(User::new)
        .orElseThrow(() -> new UsernameNotFoundException("'%s' is not found".formatted(username)));
  }

  private record User(UserInfo user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return AuthorityUtils.createAuthorityList(Streams.of(user.getRoles())
          .map(UserRole::getAuthority)
          .toArray(String[]::new));
    }

    @Override
    public String getPassword() {
      return user.getPassword().encodedValue();
    }

    @Override
    public String getUsername() {
      return user.getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return user.getStatus() != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return user.getStatus() == UserStatus.ENABLED;
    }
  }
}
