package org.backend.user.repository.interfaces;

import org.backend.user.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  <T> T findUserByUserNameIgnoreCase(String username, Class<T> type);
  <T> T findById(Long id, Class<T> type);

  @Modifying
  @Transactional
  @Query("update users set profilePicture = ?2 where id =?1")
  void updateUserProfilePicture(Long loggedInUserId, String path);

  interface Specs {
    static Specification<User> id(List<Long> id) {
      return (root, query, builder) -> root.get("id").in(id);
    }
    static Specification<User> userName(String userName) {
      return (root, query, builder) -> builder.equal(builder.lower(root.get("userName")) , userName.toLowerCase());
    }
  }


}