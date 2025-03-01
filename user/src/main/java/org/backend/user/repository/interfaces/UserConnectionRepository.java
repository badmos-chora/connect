package org.backend.user.repository.interfaces;

import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.entity.UserConnection;
import org.backend.user.enums.UserConnectionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionId>, JpaSpecificationExecutor<UserConnection> {
    interface Specs{
        public static Specification<UserConnection> connectionType(UserConnectionType type){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("connectionType"), type.name()));
        }
        public static Specification<UserConnection> getUserFollowers(Long id){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("following").get("id"), id));
        }
        public static Specification<UserConnection> getUserFollowings(Long id){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("follower").get("id"), id));
        }
    }
}