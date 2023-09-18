package com.sciencesakura.sample.domain.user;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import com.sciencesakura.sample.domain.ConflictException;
import com.sciencesakura.sample.domain.NotFoundException;
import com.sciencesakura.sample.util.MailAddresses;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service class which provides the operations for user.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserInfoRepository userInfoRepository;

  /**
   * Retrieves all users by the specified query.
   *
   * @param query the query for searching users
   * @return the page of users
   */
  @Nonnull
  @Transactional(readOnly = true)
  public Page<UserInfo> findAll(@Nonnull UserQuery query) {
    var spec = new ArrayList<Specification<UserInfo>>();
    if (StringUtils.isNotEmpty(query.getText())) {
      var pattern = '%' + query.getText() + '%';
      spec.add((root, q, cb) -> cb.or(
          cb.like(root.get(UserInfo_.emailAddress), pattern),
          cb.like(root.get(UserInfo_.name), pattern),
          cb.like(root.get(UserInfo_.description), pattern)
      ));
    }
    if (CollectionUtils.isNotEmpty(query.getStatus())) {
      spec.add((root, q, cb) -> root.get(UserInfo_.status).in(query.getStatus()));
    }
    return userInfoRepository.findAll(Specification.allOf(spec), query.getPageRequest());
  }

  /**
   * Retrieves the user by the specified ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user who has the specified ID, or empty if not found
   */
  @Nonnull
  @Transactional(readOnly = true)
  public Optional<UserInfo> findById(@Nonnull UUID id) {
    return userInfoRepository.findById(id);
  }

  /**
   * Retrieves the user by the specified email address.
   *
   * @param emailAddress the email address of the user to retrieve
   * @return the user who has the specified email address, or empty if not found
   */
  @Nonnull
  @Transactional(readOnly = true)
  public Optional<UserInfo> findByEmailAddress(@Nonnull String emailAddress) {
    return userInfoRepository.findByEmailAddress(emailAddress);
  }

  /**
   * Persists a new user.
   *
   * @param newUserInfo the new user to persist
   * @return the persisted user
   */
  @Nonnull
  public UserInfo create(@Nonnull UserInfo newUserInfo) {
    if (StringUtils.isEmpty(newUserInfo.getName())) {
      newUserInfo.setName(MailAddresses.getLocalPart(newUserInfo.getEmailAddress()));
    }
    if (newUserInfo.getStatus() == null) {
      newUserInfo.setStatus(UserStatus.TEMPORARY);
    }
    findByEmailAddress(newUserInfo.getEmailAddress()).ifPresent(u -> {
      throw new ConflictException("UserInfo", u.getEmailAddress());
    });
    return userInfoRepository.save(newUserInfo);
  }

  /**
   * Modifies the user.
   *
   * @param id          the ID of the user to modify
   * @param newUserInfo the new user to modify
   * @return the modified user
   */
  @Nonnull
  public UserInfo update(@Nonnull UUID id, @Nonnull UserInfo newUserInfo) {
    var current = findById(id).orElseThrow(() -> new NotFoundException("UserInfo", id));
    current.setPassword(newUserInfo.getPassword());
    current.setName(newUserInfo.getName());
    current.setStatus(newUserInfo.getStatus());
    current.setDescription(newUserInfo.getDescription());
    current.setRoles(mergeRoles(current.getRoles(), newUserInfo.getRoles()));
    return current;
  }

  private List<UserRole> mergeRoles(List<UserRole> currentRoles, List<UserRole> newRoles) {
    var newRoleMap = Streams.of(newRoles)
        .collect(toMap(UserRole::getRole, identity()));
    var results = Streams.of(currentRoles)
        .filter(r -> newRoleMap.remove(r.getRole()) != null)
        .collect(toCollection(ArrayList::new));
    results.addAll(newRoleMap.values());
    return results;
  }
}
