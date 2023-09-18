package com.sciencesakura.sample.domain.user;

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
class UserServiceFindTest {

  static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

  static String encodedPassword;

  @Autowired
  UserService userService;

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
        csv(dir(UserServiceFindTest.class) + "/user_info.csv")
            .withDefaultValue("password", encodedPassword)
            .build(),
        csv(dir(UserServiceFindTest.class) + "/user_role.csv").build()
    ));
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @AfterEach
  void tearDown() {
    dbSetupTracker.skipNextLaunch();
  }

  @Nested
  class findAll {

    @Test
    void by_default_query() {
      var query = new UserQuery();
      var actual = userService.findAll(query);
      assertThat(actual).satisfiesExactly(
          u -> {
            assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000004"));
            assertThat(u.getEmailAddress()).isEqualTo("xxxxx04@example.com");
            assertThat(u.getPassword().encodedValue()).isEqualTo(encodedPassword);
            assertThat(u.getName()).isEqualTo("xxxxx");
            assertThat(u.getStatus()).isEqualTo(UserStatus.DISABLED);
            assertThat(u.getDescription()).isEqualTo("xaaax");
            assertThat(u.getCreatedAt()).isEqualTo("2020-01-04T10:20:30.456");
            assertThat(u.getCreatedBy()).isEqualTo("foo");
            assertThat(u.getUpdatedAt()).isEqualTo("2020-02-04T10:20:30.456");
            assertThat(u.getUpdatedBy()).isEqualTo("bar");
            assertThat(u.getRoles()).satisfiesExactly(
                r -> {
                  assertThat(r.getUser()).isSameAs(u);
                  assertThat(r.getRole()).isEqualTo("AAA");
                  assertThat(r.getCreatedAt()).isEqualTo("2021-01-04T10:20:30.456");
                  assertThat(r.getCreatedBy()).isEqualTo("baz");
                },
                r -> {
                  assertThat(r.getUser()).isSameAs(u);
                  assertThat(r.getRole()).isEqualTo("BBB");
                  assertThat(r.getCreatedAt()).isEqualTo("2021-01-05T10:20:30.456");
                  assertThat(r.getCreatedBy()).isEqualTo("baz");
                }
            );
          },
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000003")),
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")),
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"))
      );
    }

    @Test
    void by_text() {
      var query = new UserQuery();
      query.setText("aaa");
      var actual = userService.findAll(query);
      assertThat(actual).satisfiesExactly(
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000004")),
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000003")),
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"))
      );
    }

    @Test
    void by_status() {
      var query = new UserQuery();
      query.setStatus(Set.of(UserStatus.TEMPORARY, UserStatus.ENABLED));
      var actual = userService.findAll(query);
      assertThat(actual).satisfiesExactly(
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002")),
          u -> assertThat(u.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"))
      );
    }
  }
}
