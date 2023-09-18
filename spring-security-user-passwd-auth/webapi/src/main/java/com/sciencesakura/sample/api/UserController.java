package com.sciencesakura.sample.api;

import com.sciencesakura.sample.domain.NotFoundException;
import com.sciencesakura.sample.domain.user.UserInfo;
import com.sciencesakura.sample.domain.user.UserInfo.OnCreate;
import com.sciencesakura.sample.domain.user.UserInfo.OnUpdate;
import com.sciencesakura.sample.domain.user.UserQuery;
import com.sciencesakura.sample.domain.user.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The entry point of the user API.
 */
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * {@code GET /s/users}.
   */
  @GetMapping({"/s/users", "/users"})
  public ResponseEntity<List<UserInfo>> search(@Validated UserQuery query) {
    return ResponseEntity.ok(userService.findAll(query).toList());
  }

  /**
   * {@code POST /s/users}.
   */
  @PostMapping("/s/users")
  public ResponseEntity<Void> create(@RequestBody @Validated(OnCreate.class) UserInfo body,
      UriComponentsBuilder uriBuilder) {
    var created = userService.create(body);
    var location = uriBuilder.path("/s/users/{id}").build(created.getId());
    return ResponseEntity.created(location).build();
  }

  /**
   * {@code GET /s/users/{id}}.
   */
  @GetMapping({"/s/users/{id:[0-9A-Fa-f-]{36}}", "/users/{id:[0-9A-Fa-f-]{36}}"})
  public ResponseEntity<UserInfo> get(@PathVariable UUID id) {
    return userService.findById(id).map(ResponseEntity::ok).orElseThrow(() -> new NotFoundException("UserInfo", id));
  }

  /**
   * {@code PUT /s/users/{id}}.
   */
  @PutMapping("/s/users/{id:[0-9A-Fa-f-]{36}}")
  public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Validated(OnUpdate.class) UserInfo body) {
    userService.update(id, body);
    return ResponseEntity.noContent().build();
  }
}
