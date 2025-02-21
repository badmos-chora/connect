package org.backend.user.repository.interfaces;

import org.backend.user.entity.User;
import org.backend.user.projections.UserInfoProjection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  User findUserByUserNameIgnoreCase(String username);
  <T> T findById(Long id, Class<T> type);

  interface Specs {
    static Specification<User> id(List<Long> id) {
      return (root, query, builder) -> root.get("id").in(id);
    }
  }


}