package com.sciencesakura.sample.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.sciencesakura.sample.domain.Entity;
import com.sciencesakura.sample.domain.task.Task;
import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * The entity which represents a user information.
 */
@jakarta.persistence.Entity
@Getter
@Setter
public class UserInfo extends Entity<UserInfo> {

  @NotNull(groups = {Task.OnCreate.class, Task.OnUpdate.class})
  @Id
  @GeneratedValue
  private UUID id;

  @NotBlank(groups = OnCreate.class)
  @Email
  @Column(updatable = false)
  private String emailAddress;

  @NotNull(groups = {OnCreate.class, OnUpdate.class})
  @JsonProperty(access = Access.WRITE_ONLY)
  @ToStringExclude
  private Password password;

  @NotBlank(groups = OnUpdate.class)
  @Size(max = 300)
  private String name;

  @NotNull(groups = OnUpdate.class)
  private UserStatus status;

  @Size(max = 1000)
  private String description;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @LastModifiedBy
  private String updatedBy;

  @Version
  private long version;

  @Valid
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER,
      orphanRemoval = true)
  @JoinColumn(name = "user_id", updatable = false)
  @OrderBy("role")
  @Fetch(FetchMode.SUBSELECT)
  private List<@NotNull UserRole> roles;

  /**
   * Set roles.
   *
   * @param roles the roles
   */
  public void setRoles(@Nonnull List<UserRole> roles) {
    roles.forEach(r -> r.setUser(this));
    if (this.roles == null) {
      this.roles = roles;
    } else {
      this.roles.clear();
      this.roles.addAll(roles);
    }
  }

  /**
   * The validation group for creating a user.
   */
  public interface OnCreate extends Default {

  }

  /**
   * The validation group for updating a user.
   */
  public interface OnUpdate extends Default {

  }
}
