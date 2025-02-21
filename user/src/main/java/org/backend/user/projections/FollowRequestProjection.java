package org.backend.user.projections;

import org.backend.user.enums.FollowRequestStatus;

import java.time.Instant;

/**
 * Projection for {@link org.backend.user.entity.FollowRequest}
 */
public interface FollowRequestProjection {
    Long getId();

    FollowRequestStatus getStatus();

    Instant getSentAt();

    UserInfoProjection getSenderUser();

    UserInfoProjection getReceiverUser();
}