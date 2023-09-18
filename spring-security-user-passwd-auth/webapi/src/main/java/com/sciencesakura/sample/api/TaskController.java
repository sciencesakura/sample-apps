package com.sciencesakura.sample.api;

import com.sciencesakura.sample.domain.NotFoundException;
import com.sciencesakura.sample.domain.task.Task;
import com.sciencesakura.sample.domain.task.Task.OnCreate;
import com.sciencesakura.sample.domain.task.Task.OnUpdate;
import com.sciencesakura.sample.domain.task.TaskQuery;
import com.sciencesakura.sample.domain.task.TaskService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The entry point of the task API.
 */
@RestController
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  /**
   * {@code GET /tasks}.
   */
  @GetMapping("/tasks")
  public ResponseEntity<List<Task>> search(@Validated TaskQuery query) {
    return ResponseEntity.ok(taskService.findAll(query).toList());
  }

  /**
   * {@code POST /tasks}.
   */
  @PostMapping("/tasks")
  public ResponseEntity<Void> create(@RequestBody @Validated(OnCreate.class) Task body,
      UriComponentsBuilder uriBuilder) {
    var created = taskService.create(body);
    var location = uriBuilder.path("/tasks/{id}").build(created.getId());
    return ResponseEntity.created(location).build();
  }

  /**
   * {@code GET /tasks/{id}}.
   */
  @GetMapping("/tasks/{id:[0-9A-Fa-f-]{36}}")
  public ResponseEntity<Task> get(@PathVariable UUID id) {
    return taskService.findById(id).map(ResponseEntity::ok).orElseThrow(() -> new NotFoundException("Task", id));
  }

  /**
   * {@code PUT /tasks/{id}}.
   */
  @PutMapping("/tasks/{id:[0-9A-Fa-f-]{36}}")
  public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Validated(OnUpdate.class) Task body) {
    taskService.update(id, body);
    return ResponseEntity.noContent().build();
  }

  /**
   * {@code DELETE /tasks/{id}}.
   */
  @DeleteMapping("/tasks/{id:[0-9A-Fa-f-]{36}}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    taskService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
