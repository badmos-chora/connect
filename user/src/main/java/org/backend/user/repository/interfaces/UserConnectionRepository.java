package org.backend.user.repository.interfaces;

import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.entity.UserConnection;
import org.backend.user.enums.UserConnectionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionId>, JpaSpecificationExecutor<UserConnection> {
    @Modifying
    @Query("update user_connections uc set uc.connectionType = ?3 where (uc.follower.id = ?1 and uc.following.id = ?2)")
    public int updateConnectionsStatus(Long user1, Long user2, UserConnectionType connectionType);
    interface Specs{
        public static Specification<UserConnection> connectionType(UserConnectionType type){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("connectionType"), type.name()));
        }
        public static Specification<UserConnection> followingIdEquals(Long id){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("following").get("id"), id));
        }
        public static Specification<UserConnection> followerIdEquals(Long id){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("follower").get("id"), id));
        }
        public static Specification<UserConnection> connectionTypeIn(List<String> type){
            return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("connectionType")).value(type));
        }

    }
}