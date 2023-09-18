package com.sciencesakura.sample.domain.task;

import com.sciencesakura.sample.domain.NotFoundException;
import com.sciencesakura.sample.domain.user.UserInfo;
import com.sciencesakura.sample.domain.user.UserInfo_;
import com.sciencesakura.sample.domain.user.UserService;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service class which provides the operations for task.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  private final UserService userService;

  /**
   * Retrieves all tasks by the specified query.
   *
   * @param query the query for searching tasks
   * @return the page of tasks
   */
  @Nonnull
  @Transactional(readOnly = true)
  public Page<Task> findAll(@Nonnull TaskQuery query) {
    var spec = new ArrayList<Specification<Task>>();
    if (StringUtils.isNotEmpty(query.getText())) {
      var pattern = '%' + query.getText() + '%';
      spec.add((root, q, cb) -> cb.or(
          cb.like(root.get(Task_.title), pattern),
          cb.like(root.get(Task_.description), pattern)
      ));
    }
    if (StringUtils.isNotEmpty(query.getAssignee())) {
      spec.add((root, q, cb) -> cb.equal(root.get(Task_.assignee).get(UserInfo_.emailAddress),
          query.getAssignee()));
    }
    if (CollectionUtils.isNotEmpty(query.getStatus())) {
      spec.add((root, q, cb) -> root.get(Task_.status).in(query.getStatus()));
    }
    return taskRepository.findAll(Specification.allOf(spec), query.getPageRequest());
  }

  /**
   * Retrieves the task by the specified ID.
   *
   * @param id the ID of the task to retrieve
   * @return the task which has the specified ID, or empty if not found
   */
  @Nonnull
  @Transactional(readOnly = true)
  public Optional<Task> findById(@Nonnull UUID id) {
    return taskRepository.findById(id);
  }

  /**
   * Persists a new task.
   *
   * @param newTask the new task to persist
   * @return the persisted task
   */
  @Nonnull
  public Task create(@Nonnull Task newTask) {
    newTask.setAssignee(findAssignee(newTask.getAssignee().getId()));
    if (newTask.getPriority() == null) {
      newTask.setPriority(Priority.NORMAL);
    }
    if (newTask.getStatus() == null) {
      newTask.setStatus(TaskStatus.CREATED);
    }
    return taskRepository.save(newTask);
  }

  /**
   * Modifies the task.
   *
   * @param id      the ID of the task to modify
   * @param newTask the new task to modify
   * @return the modified task
   */
  @Nonnull
  public Task update(@Nonnull UUID id, @Nonnull Task newTask) {
    var current = findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    current.setTitle(newTask.getTitle());
    if (!current.getAssignee().getId().equals(newTask.getAssignee().getId())) {
      current.setAssignee(findAssignee(newTask.getAssignee().getId()));
    }
    current.setDueDate(newTask.getDueDate());
    current.setPriority(newTask.getPriority());
    current.setStatus(newTask.getStatus());
    current.setDescription(newTask.getDescription());
    return current;
  }

  /**
   * Deletes the task by the specified ID.
   *
   * @param id the ID of the task to delete
   */
  public void delete(@Nonnull UUID id) {
    taskRepository.deleteById(id);
  }

  private UserInfo findAssignee(UUID assigneeId) {
    return userService.findById(assigneeId)
        .orElseThrow(() -> new NotFoundException("UserInfo", assigneeId));
  }
}
