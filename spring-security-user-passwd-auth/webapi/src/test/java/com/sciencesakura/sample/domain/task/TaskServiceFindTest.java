package com.sciencesakura.sample.domain.task;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.sciencesakura.dbsetup.csv.Import.csv;
import static com.sciencesakura.sample.test.TestUtils.dir;
import static org.assertj.core.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.Destination;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class TaskServiceFindTest {

  static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

  static String encodedPassword;

  @Autowired
  TaskService taskService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  Destination destination;

  @BeforeEach
  void setUp() {
    if (encodedPassword == null) {
      encodedPassword = encoder.encode("Abc_123");
    }
    var dbSetup = new DbSetup(destination, sequenceOf(
        deleteAllFrom("user_info"),
        csv(dir(TaskServiceFindTest.class) + "/user_info.csv")
            .withDefaultValue("password", encodedPassword)
            .build(),
        csv(dir(TaskServiceFindTest.class) + "/task.csv").build()
    ));
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @Nested
  class findAll {

    @AfterEach
    void tearDown() {
      dbSetupTracker.skipNextLaunch();
    }

    @Test
    void by_default_query() {
      var query = new TaskQuery();
      var actual = taskService.findAll(query);
      assertThat(actual).satisfiesExactly(
          t -> {
            assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000003"));
            assertThat(t.getTitle()).isEqualTo("xxxxx");
            assertThat(t.getAssignee()).satisfies(
                u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")));
            assertThat(t.getDueDate()).isEqualTo("2022-01-01");
            assertThat(t.getPriority()).isEqualTo(Priority.NORMAL);
            assertThat(t.getStatus()).isEqualTo(TaskStatus.COMPLETED);
            assertThat(t.getDescription()).isEqualTo("xaaax");
            assertThat(t.getCreatedAt()).isEqualTo("2022-02-03T10:20:30.456");
            assertThat(t.getCreatedBy()).isEqualTo("foo");
            assertThat(t.getUpdatedAt()).isEqualTo("2022-03-03T10:20:30.456");
            assertThat(t.getUpdatedBy()).isEqualTo("bar");
          },
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")),
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"))
      );
    }

    @Test
    void by_text() {
      var query = new TaskQuery();
      query.setText("aaa");
      var actual = taskService.findAll(query);
      assertThat(actual).satisfiesExactly(
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000003")),
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"))
      );
    }

    @Test
    void by_assignee() {
      var query = new TaskQuery();
      query.setAssignee("user01@example.com");
      var actual = taskService.findAll(query);
      assertThat(actual).satisfiesExactly(
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"))
      );
    }

    @Test
    void by_status() {
      var query = new TaskQuery();
      query.setStatus(Set.of(TaskStatus.CREATED, TaskStatus.WORK_IN_PROGRESS));
      var actual = taskService.findAll(query);
      assertThat(actual).satisfiesExactly(
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")),
          t -> assertThat(t.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"))
      );
    }
  }
}
