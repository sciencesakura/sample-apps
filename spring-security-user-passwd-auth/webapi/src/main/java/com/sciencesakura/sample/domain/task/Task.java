package com.sciencesakura.sample.domain.task;

import com.sciencesakura.sample.domain.Entity;
import com.sciencesakura.sample.domain.user.UserInfo;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * The entity which represents a task.
 */
@jakarta.persistence.Entity
@Getter
@Setter
public class Task extends Entity<Task> {

  @Id
  @GeneratedValue
  private UUID id;

  @NotBlank(groups = {OnCreate.class, OnUpdate.class})
  @Size(max = 300)
  private String title;

  @Valid
  @NotNull(groups = {OnCreate.class, OnUpdate.class})
  @ManyToOne
  @JoinColumn(name = "assignee_id")
  private UserInfo assignee;

  @NotNull(groups = {OnCreate.class, OnUpdate.class})
  @FutureOrPresent
  private LocalDate dueDate;

  @NotNull(groups = OnUpdate.class)
  private Priority priority;

  @NotNull(groups = OnUpdate.class)
  private TaskStatus status;

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

  /**
   * The validation group for creating a task.
   */
  public interface OnCreate extends Default {

  }

  /**
   * The validation group for updating a task.
   */
  public interface OnUpdate extends Default {

  }
}
