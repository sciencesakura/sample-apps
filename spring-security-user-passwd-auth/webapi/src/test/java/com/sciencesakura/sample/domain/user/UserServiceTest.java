package com.sciencesakura.sample.domain.user;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.sciencesakura.dbsetup.csv.Import.csv;
import static com.sciencesakura.sample.test.TestUtils.dir;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.db.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.Destination;
import com.sciencesakura.sample.domain.ConflictException;
import com.sciencesakura.sample.domain.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
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
class UserServiceTest {

  static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

  static String encodedPassword;

  @Autowired
  UserService userService;

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
        csv(dir(UserServiceTest.class) + "/user_info.csv")
            .withDefaultValue("password", encodedPassword)
            .build(),
        csv(dir(UserServiceTest.class) + "/user_role.csv").build()
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
    void return_user() {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var actual = userService.findById(id);
      assertThat(actual).hasValueSatisfying(u -> {
        assertThat(u.getId()).isEqualTo(id);
        assertThat(u.getEmailAddress()).isEqualTo("user02@example.com");
        assertThat(u.getPassword().encodedValue()).isEqualTo(encodedPassword);
        assertThat(u.getName()).isEqualTo("User 02");
        assertThat(u.getStatus()).isEqualTo(UserStatus.ENABLED);
        assertThat(u.getDescription()).isEqualTo("Description 02");
        assertThat(u.getCreatedAt()).isEqualTo("2020-01-02T10:20:30.456");
        assertThat(u.getCreatedBy()).isEqualTo("foo");
        assertThat(u.getUpdatedAt()).isEqualTo("2020-02-02T10:20:30.456");
        assertThat(u.getUpdatedBy()).isEqualTo("bar");
        assertThat(u.getRoles()).satisfiesExactly(
            r -> {
              assertThat(r.getUser()).isSameAs(u);
              assertThat(r.getRole()).isEqualTo("AAA");
              assertThat(r.getCreatedAt()).isEqualTo("2021-01-01T10:20:30.456");
              assertThat(r.getCreatedBy()).isEqualTo("baz");
            },
            r -> {
              assertThat(r.getUser()).isSameAs(u);
              assertThat(r.getRole()).isEqualTo("BBB");
              assertThat(r.getCreatedAt()).isEqualTo("2021-01-02T10:20:30.456");
              assertThat(r.getCreatedBy()).isEqualTo("baz");
            }
        );
      });
    }

    @Test
    void return_user_that_has_no_optional_props() {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var actual = userService.findById(id);
      assertThat(actual).hasValueSatisfying(u -> {
        assertThat(u.getId()).isEqualTo(id);
        assertThat(u.getEmailAddress()).isEqualTo("user01@example.com");
        assertThat(u.getPassword().encodedValue()).isEqualTo(encodedPassword);
        assertThat(u.getName()).isEqualTo("User 01");
        assertThat(u.getStatus()).isEqualTo(UserStatus.ENABLED);
        assertThat(u.getDescription()).isNull();
        assertThat(u.getCreatedAt()).isEqualTo("2020-01-01T10:20:30.456");
        assertThat(u.getCreatedBy()).isEqualTo("foo");
        assertThat(u.getUpdatedAt()).isEqualTo("2020-02-01T10:20:30.456");
        assertThat(u.getUpdatedBy()).isEqualTo("bar");
        assertThat(u.getRoles()).isNullOrEmpty();
      });
    }

    @Test
    void return_empty_if_not_found() {
      var id = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      var actual = userService.findById(id);
      assertThat(actual).isEmpty();
    }
  }

  @Nested
  class findByEmailAddress {

    @AfterEach
    void tearDown() {
      dbSetupTracker.skipNextLaunch();
    }

    @Test
    void return_user() {
      var emailAddress = "user02@example.com";
      var actual = userService.findByEmailAddress(emailAddress);
      assertThat(actual).hasValueSatisfying(u -> {
        assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertThat(u.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(u.getPassword().encodedValue()).isEqualTo(encodedPassword);
        assertThat(u.getName()).isEqualTo("User 02");
        assertThat(u.getStatus()).isEqualTo(UserStatus.ENABLED);
        assertThat(u.getDescription()).isEqualTo("Description 02");
        assertThat(u.getCreatedAt()).isEqualTo("2020-01-02T10:20:30.456");
        assertThat(u.getCreatedBy()).isEqualTo("foo");
        assertThat(u.getUpdatedAt()).isEqualTo("2020-02-02T10:20:30.456");
        assertThat(u.getUpdatedBy()).isEqualTo("bar");
        assertThat(u.getRoles()).satisfiesExactly(
            r -> {
              assertThat(r.getUser()).isSameAs(u);
              assertThat(r.getRole()).isEqualTo("AAA");
              assertThat(r.getCreatedAt()).isEqualTo("2021-01-01T10:20:30.456");
              assertThat(r.getCreatedBy()).isEqualTo("baz");
            },
            r -> {
              assertThat(r.getUser()).isSameAs(u);
              assertThat(r.getRole()).isEqualTo("BBB");
              assertThat(r.getCreatedAt()).isEqualTo("2021-01-02T10:20:30.456");
              assertThat(r.getCreatedBy()).isEqualTo("baz");
            }
        );
      });
    }

    @Test
    void return_user_that_has_no_optional_props() {
      var emailAddress = "user01@example.com";
      var actual = userService.findByEmailAddress(emailAddress);
      assertThat(actual).hasValueSatisfying(u -> {
        assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertThat(u.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(u.getPassword().encodedValue()).isEqualTo(encodedPassword);
        assertThat(u.getName()).isEqualTo("User 01");
        assertThat(u.getStatus()).isEqualTo(UserStatus.ENABLED);
        assertThat(u.getDescription()).isNull();
        assertThat(u.getCreatedAt()).isEqualTo("2020-01-01T10:20:30.456");
        assertThat(u.getCreatedBy()).isEqualTo("foo");
        assertThat(u.getUpdatedAt()).isEqualTo("2020-02-01T10:20:30.456");
        assertThat(u.getUpdatedBy()).isEqualTo("bar");
        assertThat(u.getRoles()).isNullOrEmpty();
      });
    }

    @Test
    void return_empty_if_not_found() {
      var emailAddress = "user99@example.com";
      var actual = userService.findByEmailAddress(emailAddress);
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
    void add_user() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var user = new UserInfo();
      user.setEmailAddress("user99@example.com");
      user.setPassword(() -> encodedPassword);
      user.setName("User 99");
      user.setStatus(UserStatus.ENABLED);
      user.setDescription("Description 99");
      user.setRoles(List.of(new UserRole("AAA"), new UserRole("BBB")));
      var actual = userService.create(user);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getEmailAddress()).isEqualTo("user99@example.com");
      assertThat(actual.getPassword().encodedValue()).isEqualTo(encodedPassword);
      assertThat(actual.getName()).isEqualTo("User 99");
      assertThat(actual.getStatus()).isEqualTo(UserStatus.ENABLED);
      assertThat(actual.getDescription()).isEqualTo("Description 99");
      assertThat(actual.getCreatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getCreatedBy()).isEqualTo("mock user");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(actual.getRoles()).satisfiesExactly(
          r -> {
            assertThat(r.getUser()).isSameAs(actual);
            assertThat(r.getRole()).isEqualTo("AAA");
            assertThat(r.getCreatedAt()).isAfterOrEqualTo(timestamp);
            assertThat(r.getCreatedBy()).isEqualTo("mock user");
          },
          r -> {
            assertThat(r.getUser()).isSameAs(actual);
            assertThat(r.getRole()).isEqualTo("BBB");
            assertThat(r.getCreatedAt()).isAfterOrEqualTo(timestamp);
            assertThat(r.getCreatedBy()).isEqualTo("mock user");
          }
      );
      assertThat(changes.setEndPointNow())
          .hasNumberOfChanges(3)
          .changeOfCreationOnTable("user_info")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfCreationOnTable("user_role")
          .rowAtEndPoint()
          .value("user_id").isEqualTo(actual.getId())
          .value("role").isEqualTo("AAA")
          .changeOfCreationOnTable("user_role")
          .rowAtEndPoint()
          .value("user_id").isEqualTo(actual.getId())
          .value("role").isEqualTo("BBB");
    }

    @Test
    void add_user_that_has_no_optional_props() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var user = new UserInfo();
      user.setEmailAddress("user99@example.com");
      user.setPassword(() -> encodedPassword);
      var actual = userService.create(user);
      assertThat(actual.getId()).isNotNull();
      assertThat(actual.getEmailAddress()).isEqualTo("user99@example.com");
      assertThat(actual.getPassword().encodedValue()).isEqualTo(encodedPassword);
      assertThat(actual.getName()).isEqualTo("user99");
      assertThat(actual.getStatus()).isEqualTo(UserStatus.TEMPORARY);
      assertThat(actual.getDescription()).isNull();
      assertThat(actual.getCreatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getCreatedBy()).isEqualTo("mock user");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(actual.getRoles()).isNullOrEmpty();
      assertThat(changes.setEndPointNow())
          .hasNumberOfChanges(1)
          .changeOfCreationOnTable("user_info")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId());
    }

    @Test
    void throw_exception_if_emailAddress_is_already_used() {
      changes.setStartPointNow();
      var user = new UserInfo();
      user.setEmailAddress("user02@example.com");
      user.setPassword(() -> encodedPassword);
      assertThatThrownBy(() -> userService.create(user)).isInstanceOfSatisfying(ConflictException.class, e -> {
        assertThat(e.getResource()).isEqualTo("UserInfo");
        assertThat(e.getIdentifier()).isEqualTo("user02@example.com");
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
    void modify_user() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var user = new UserInfo();
      user.setPassword(() -> encodedPassword);
      user.setName("User 99");
      user.setStatus(UserStatus.LOCKED);
      user.setDescription("Description 99");
      user.setRoles(List.of(new UserRole("BBB"), new UserRole("CCC")));
      var actual = userService.update(id, user);
      assertThat(actual.getId()).isEqualTo(id);
      assertThat(actual.getEmailAddress()).isEqualTo("user02@example.com");
      assertThat(actual.getPassword().encodedValue()).isEqualTo(encodedPassword);
      assertThat(actual.getName()).isEqualTo("User 99");
      assertThat(actual.getStatus()).isEqualTo(UserStatus.LOCKED);
      assertThat(actual.getDescription()).isEqualTo("Description 99");
      assertThat(actual.getCreatedAt()).isEqualTo("2020-01-02T10:20:30.456");
      assertThat(actual.getCreatedBy()).isEqualTo("foo");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(actual.getRoles()).satisfiesExactly(
          r -> {
            assertThat(r.getUser()).isSameAs(actual);
            assertThat(r.getRole()).isEqualTo("BBB");
            assertThat(r.getCreatedAt()).isEqualTo("2021-01-02T10:20:30.456");
            assertThat(r.getCreatedBy()).isEqualTo("baz");
          },
          r -> {
            assertThat(r.getUser()).isSameAs(actual);
            assertThat(r.getRole()).isEqualTo("CCC");
            assertThat(r.getCreatedAt()).isAfterOrEqualTo(timestamp);
            assertThat(r.getCreatedBy()).isEqualTo("mock user");
          }
      );
      assertThat(changes.setEndPointNow())
          .hasNumberOfChanges(3)
          .changeOfModificationOnTable("user_info")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfDeletionOnTable("user_role")
          .rowAtStartPoint()
          .value("user_id").isEqualTo(actual.getId())
          .value("role").isEqualTo("AAA")
          .changeOfCreationOnTable("user_role")
          .rowAtEndPoint()
          .value("user_id").isEqualTo(actual.getId())
          .value("role").isEqualTo("CCC");
    }

    @Test
    void modify_user_to_remove_optional_props() {
      changes.setStartPointNow();
      var timestamp = LocalDateTime.now();
      var id = UUID.fromString("00000000-0000-0000-0000-000000000002");
      var user = new UserInfo();
      user.setPassword(() -> encodedPassword);
      user.setName("User 99");
      user.setStatus(UserStatus.LOCKED);
      var actual = userService.update(id, user);
      assertThat(actual.getId()).isEqualTo(id);
      assertThat(actual.getEmailAddress()).isEqualTo("user02@example.com");
      assertThat(actual.getPassword().encodedValue()).isEqualTo(encodedPassword);
      assertThat(actual.getName()).isEqualTo("User 99");
      assertThat(actual.getStatus()).isEqualTo(UserStatus.LOCKED);
      assertThat(actual.getDescription()).isNull();
      assertThat(actual.getCreatedAt()).isEqualTo("2020-01-02T10:20:30.456");
      assertThat(actual.getCreatedBy()).isEqualTo("foo");
      assertThat(actual.getUpdatedAt()).isAfterOrEqualTo(timestamp);
      assertThat(actual.getUpdatedBy()).isEqualTo("mock user");
      assertThat(actual.getRoles()).isNullOrEmpty();
      assertThat(changes.setEndPointNow())
          .hasNumberOfChanges(3)
          .changeOfModificationOnTable("user_info")
          .rowAtEndPoint()
          .value("id").isEqualTo(actual.getId())
          .changeOfDeletionOnTable("user_role")
          .rowAtStartPoint()
          .value("user_id").isEqualTo(actual.getId())
          .value("role").isEqualTo("AAA")
          .changeOfDeletionOnTable("user_role")
          .rowAtStartPoint()
          .value("user_id").isEqualTo(actual.getId())
          .value("role").isEqualTo("BBB");
    }

    @Test
    void throw_exception_if_not_found() {
      changes.setStartPointNow();
      var id = UUID.fromString("00000000-0000-0000-0000-ffffffffffff");
      var user = new UserInfo();
      user.setPassword(() -> encodedPassword);
      user.setName("User 99");
      user.setStatus(UserStatus.LOCKED);
      assertThatThrownBy(() -> userService.update(id, user)).isInstanceOfSatisfying(NotFoundException.class, e -> {
        assertThat(e.getResource()).isEqualTo("UserInfo");
        assertThat(e.getIdentifier()).isEqualTo(id);
      });
      assertThat(changes.setEndPointNow()).hasNumberOfChanges(0);
      dbSetupTracker.skipNextLaunch();
    }
  }
}
