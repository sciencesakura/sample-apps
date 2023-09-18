package com.sciencesakura.sample.domain.task;

import com.sciencesakura.sample.domain.Query;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort.Order;

/**
 * The query for searching tasks.
 */
@Getter
@Setter
public class TaskQuery extends Query<TaskQuery> {

  @Size(max = 1000)
  private String text;

  @Email
  private String assignee;

  private Set<@NotNull TaskStatus> status;

  public TaskQuery() {
    setSize(20);
    setOrder(List.of(Order.asc(Task_.DUE_DATE), Order.desc(Task_.PRIORITY), Order.desc(Task_.UPDATED_AT)));
  }
}
