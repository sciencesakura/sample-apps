package com.sciencesakura.sample.domain.task;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.sciencesakura.dbsetup.csv.Import.csv;
import static com.sciencesakura.sample.test.TestUtils.dir;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.db.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.Destination;
import com.sciencesakura.sample.domain.NotFoundException;
import com.sciencesakura.sample.domain.user.UserInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class TaskServiceTest {

  static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

  static String encodedPassword;

  @Autowired
  TaskService taskService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  Destination destination;

  @Autowired
  DataSource dataSource;

  @BeforeEach
  void setUp() {
    if (encodedPassword == null) {
      encodedPassword = encoder.encode("Abc_123");
    }
    var dbSetup = new DbSetup(destination, sequenceOf(
        deleteAllFrom("user_info"),
        csv(dir(TaskServiceTest.class) + "/user_info.csv")
            .withDefaultValue("password", encodedPassword)
            .build(),
        csv(dir(TaskServiceTest.class) + "/task.csv").build()
    ));
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @Nested
  class findById {

    @AfterEach
    void tearDown() {
      dbSetupTracker.skipNextLaunch();
    }

    @Test
    void return_task() {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var actual = taskService.findById(id);
      assertThat(actual).hasValueSatisfying(t -> {
        assertThat(t.getId()).isEqualTo(id);
        assertThat(t.getTitle()).isEqualTo("Task 02");
        assertThat(t.getAssignee()).satisfies(
            u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")));
        assertThat(t.getDueDate()).isEqualTo("2022-01-02");
        assertThat(t.getPriority()).isEqualTo(Priority.HIGHER);
        assertThat(t.getStatus()).isEqualTo(TaskStatus.WORK_IN_PROGRESS);
        assertThat(t.getDescription()).isEqualTo("Description 02");
        assertThat(t.getCreatedAt()).isEqualTo("2022-02-02T10:20:30.456");
        assertThat(t.getCreatedBy()).isEqualTo("foo");
        assertThat(t.getUpdatedAt()).isEqualTo("2022-03-02T10:20:30.456");
        assertThat(t.getUpdatedBy()).isEqualTo("bar");
      });
    }

    @Test
    void return_task_that_has_no_optional_props() {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var actual = taskService.findById(id);
      assertThat(actual).hasValueSatisfying(t -> {
        assertThat(t.getId()).isEqualTo(id);
        assertThat(t.getTitle()).isEqualTo("Task 01");
        assertThat(t.getAssignee()).satisfies(
            u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001")));
        assertThat(t.getDueDate()).isEqualTo("2022-01-01");
        assertThat(t.getPriority()).isEqualTo(Priority.HIGHER);
        assertThat(t.getStatus()).isEqualTo(TaskStatus.WORK_IN_PROGRESS);
        assertThat(t.getDescription()).isNull();
        assertThat(t.getCreatedAt()).isEqualTo("2022-02-01T10:20:30.456");
        assertThat(t.getCreatedBy()).isEqualTo("foo");
        assertThat(t.getUpdatedAt()).isEqualTo("2022-03-01T10:20:30.456");
        assertThat(t.getUpdatedBy()).isEqualTo("bar");
      });
    }

    @Test
    void return_empty_if_not_found() {
      var id = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      var actual = taskService.findById(id);
      assertThat(actual).isEmpty();
    }
  }

  @Nested
  @WithMockUser("mock user")
  class create {

    Changes changes;

    @BeforeEach
    void setUp() {
      changes = new Changes(dataSource);
    }

    @Test
    void add_task() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      task.setPriority(Priority.HIGHER);
      task.setStatus(TaskStatus.WORK_IN_PROGRESS);
      task.setDescription("Description 99");
      var actual = taskService.create(task);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getTitle()).isEqualTo("Task 99");
      assertThat(actual.getAssignee()).satisfies(a -> assertThat(a.getId()).isEqualTo(assigneeId));
      assertThat(actual.getDueDate()).isEqualTo(dueDate);
      assertThat(actual.getPriority()).isEqualTo(Priority.HIGHER);
      assertThat(actual.getStatus()).isEqualTo(TaskStatus.WORK_IN_PROGRESS);
      assertThat(actual.getDescription()).isEqualTo("Description 99");
      assertThat(actual.getCreatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getCreatedBy()).isEqualTo("mock user");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfCreationOnTable("task")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId());
    }

    @Test
    void add_task_that_has_no_optional_props() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      var actual = taskService.create(task);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getTitle()).isEqualTo("Task 99");
      assertThat(actual.getAssignee()).satisfies(a -> assertThat(a.getId()).isEqualTo(assigneeId));
      assertThat(actual.getDueDate()).isEqualTo(dueDate);
      assertThat(actual.getPriority()).isEqualTo(Priority.NORMAL);
      assertThat(actual.getStatus()).isEqualTo(TaskStatus.CREATED);
      assertThat(actual.getDescription()).isNull();
      assertThat(actual.getCreatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getCreatedBy()).isEqualTo("mock user");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfCreationOnTable("task")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId());
    }

    @Test
    void throw_exception_if_assignee_does_not_exist() {
      changes.setStartPointNow();
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      assertThatThrownBy(() -> taskService.create(task)).isInstanceOfSatisfying(NotFoundException.class, e -> {
        assertThat(e.getResource()).isEqualTo("UserInfo");
        assertThat(e.getIdentifier()).isEqualTo(assigneeId);
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }
  }

  @Nested
  @WithMockUser("mock user")
  class update {

    Changes changes;

    @BeforeEach
    void setUp() {
      changes = new Changes(dataSource);
    }

    @Test
    void modify_task() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setId(id);
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      task.setPriority(Priority.HIGHEST);
      task.setStatus(TaskStatus.COMPLETED);
      task.setDescription("Description 99");
      var actual = taskService.update(id, task);
      assertThat(actual.getId()).isEqualTo(id);
      assertThat(actual.getTitle()).isEqualTo("Task 99");
      assertThat(actual.getAssignee()).satisfies(a -> assertThat(a.getId()).isEqualTo(assigneeId));
      assertThat(actual.getDueDate()).isEqualTo(dueDate);
      assertThat(actual.getPriority()).isEqualTo(Priority.HIGHEST);
      assertThat(actual.getStatus()).isEqualTo(TaskStatus.COMPLETED);
      assertThat(actual.getDescription()).isEqualTo("Description 99");
      assertThat(actual.getCreatedAt()).isEqualTo("2022-02-02T10:20:30.456");
      assertThat(actual.getCreatedBy()).isEqualTo("foo");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfModificationOnTable("task")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId());
    }

    @Test
    void modify_task_to_remove_optional_props() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setId(id);
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      task.setPriority(Priority.HIGHEST);
      task.setStatus(TaskStatus.COMPLETED);
      var actual = taskService.update(id, task);
      assertThat(actual.getId()).isEqualTo(id);
      assertThat(actual.getTitle()).isEqualTo("Task 99");
      assertThat(actual.getAssignee()).satisfies(a -> assertThat(a.getId()).isEqualTo(assigneeId));
      assertThat(actual.getDueDate()).isEqualTo(dueDate);
      assertThat(actual.getPriority()).isEqualTo(Priority.HIGHEST);
      assertThat(actual.getStatus()).isEqualTo(TaskStatus.COMPLETED);
      assertThat(actual.getDescription()).isNull();
      assertThat(actual.getCreatedAt()).isEqualTo("2022-02-02T10:20:30.456");
      assertThat(actual.getCreatedBy()).isEqualTo("foo");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfModificationOnTable("task")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId());
    }

    @Test
    void throw_exception_if_assignee_does_not_exist() {
      changes.setStartPointNow();
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setId(id);
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      task.setPriority(Priority.HIGHEST);
      task.setStatus(TaskStatus.COMPLETED);
      assertThatThrownBy(() -> taskService.update(id, task)).isInstanceOfSatisfying(NotFoundException.class, e -> {
        assertThat(e.getResource()).isEqualTo("UserInfo");
        assertThat(e.getIdentifier()).isEqualTo(assigneeId);
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }

    @Test
    void throw_exception_if_not_found() {
      changes.setStartPointNow();
      var id = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      var assigneeId = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var assignee = new UserInfo();
      assignee.setId(assigneeId);
      var dueDate = LocalDate.now();
      var task = new Task();
      task.setId(id);
      task.setTitle("Task 99");
      task.setAssignee(assignee);
      task.setDueDate(dueDate);
      task.setPriority(Priority.HIGHEST);
      task.setStatus(TaskStatus.COMPLETED);
      assertThatThrownBy(() -> taskService.update(id, task)).isInstanceOfSatisfying(NotFoundException.class, e -> {
        assertThat(e.getResource()).isEqualTo("Task");
        assertThat(e.getIdentifier()).isEqualTo(id);
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }
  }

  @Nested
  class delete {

    Changes changes;

    @BeforeEach
    void setUp() {
      changes = new Changes(dataSource);
    }

    @Test
    void remove_task() {
      changes.setStartPointNow();
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      taskService.delete(id);
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(1)
          .changeOfDeletionOnTable("task")
          .rowAtStartPoint()
          .value("id").isEqualTo(id);
    }

    @Test
    void does_not_throw_exception_if_not_found() {
      changes.setStartPointNow();
      var id = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      assertThatNoException().isThrownBy(() -> taskService.delete(id));
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }
  }
}
