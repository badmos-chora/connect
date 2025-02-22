package org.backend.user.repository.interfaces;

import jakarta.persistence.criteria.JoinType;
import org.backend.user.entity.FollowRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> , JpaSpecificationExecutor<FollowRequest> {
    interface  Specs {
        static Specification<FollowRequest> senderId(Long senderId) {
            return (root, query, builder) -> builder.equal(root.get("senderUser").get("id"), senderId);
        }
        static Specification<FollowRequest> receiverId(Long receiverId) {
            return (root, query, builder) -> builder.equal(root.get("receiverUser").get("id"), receiverId);
        }
        static Specification<FollowRequest> fetchSenderAndReceiver() {
            return (root, query, cb) -> {
                root.fetch("senderUser", JoinType.LEFT);
                root.fetch("receiverUser", JoinType.LEFT);
                query.distinct(true);
                return cb.conjunction();
            };
        }

    }
}