package org.backend.user.repository;

import org.backend.user.entity.User;
import org.backend.user.projections.UserInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findUserByUserNameIgnoreCase(String username);
  UserInfoProjection findUserInfoProjectionById(Long id);
}