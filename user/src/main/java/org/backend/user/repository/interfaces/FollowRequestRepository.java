package org.backend.user.repository.interfaces;

import jakarta.persistence.criteria.JoinType;
import org.backend.user.entity.FollowRequest;
import org.backend.user.enums.FollowRequestStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> , JpaSpecificationExecutor<FollowRequest> {

    @Modifying
    @Transactional
    @Query("update follow_request fr set fr.status = ?3 where fr.receiverUser.id = ?1 and fr.senderUser.id = ?2 and fr.status = 'PENDING'")
    public int updateFollowRequestStatus(Long receiverId, Long senderId, FollowRequestStatus status);


    interface  Specs {
        static Specification<FollowRequest> senderId(Long senderId) {
            return (root, query, builder) -> builder.equal(root.get("senderUser").get("id"), senderId);
        }
        static Specification<FollowRequest> receiverId(Long receiverId) {
            return (root, query, builder) -> builder.equal(root.get("receiverUser").get("id"), receiverId);
        }
        static Specification<FollowRequest> statusIn(FollowRequestStatus status) {
            return (root, query, cb) -> cb.equal(root.get("status"), status.name());
        }

    }
}