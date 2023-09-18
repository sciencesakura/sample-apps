package com.sciencesakura.sample.domain.user;

import com.sciencesakura.sample.domain.Query;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort.Order;

/**
 * The query for searching users.
 */
@Getter
@Setter
public class UserQuery extends Query<UserQuery> {

  @Size(max = 1000)
  private String text;

  private Set<@NotNull UserStatus> status;

  public UserQuery() {
    setSize(20);
    setOrder(List.of(Order.desc(UserInfo_.CREATED_AT), Order.asc(UserInfo_.EMAIL_ADDRESS)));
  }
}
