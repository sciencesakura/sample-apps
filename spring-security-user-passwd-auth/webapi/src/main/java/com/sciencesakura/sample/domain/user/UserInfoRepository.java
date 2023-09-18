package com.sciencesakura.sample.domain.user;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The repository in which user information is stored.
 */
interface UserInfoRepository extends JpaRepository<UserInfo, UUID>,
    JpaSpecificationExecutor<UserInfo> {

  @Nonnull
  Optional<UserInfo> findByEmailAddress(@Nonnull String emailAddress);
}
