package com.sciencesakura.sample.domain.user;

import com.sciencesakura.sample.domain.Entity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

/**
 * The entity which represents a user role.
 */
@jakarta.persistence.Entity
@IdClass(UserRole.Key.class)
@Getter
@Setter
public class UserRole extends Entity<UserRole> {

  @Id
  @ManyToOne
  @ToStringExclude
  private UserInfo user;

  @NotBlank
  @Size(max = 50)
  @Pattern(regexp = "^(?!ROLE_)[A-Z_]+$")
  @Id
  private String role;

  @CreatedDate
  private LocalDateTime createdAt;

  @CreatedBy
  private String createdBy;

  protected UserRole() {
  }

  public UserRole(@Nonnull String role) {
    this.role = role;
  }

  @Nonnull
  public String getAuthority() {
    return "ROLE_" + role;
  }

  @Getter
  @Setter
  static class Key implements Serializable {

    private UserInfo user;

    private String role;

    protected Key() {
    }

    private Key(UserInfo user, String role) {
      this.user = user;
      this.role = role;
    }

    @Override
    public int hashCode() {
      return Objects.hash(user == null ? null : user.getId(), role);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Key k) {
        if (user == null) {
          return k.user == null && Objects.equals(role, k.role);
        } else {
          return k.user != null && Objects.equals(user.getId(), k.user.getId())
              && Objects.equals(role, k.role);
        }
      }
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(new Key(user, role), createdAt, createdBy);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof UserRole r) {
      return Arrays.equals(
          new Object[]{new Key(user, role), createdAt, createdBy},
          new Object[]{new Key(r.user, r.role), r.createdAt, r.createdBy}
      );
    }
    return false;
  }
}
