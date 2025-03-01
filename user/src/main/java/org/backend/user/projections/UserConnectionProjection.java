package org.backend.user.projections;

import java.time.Instant;

/**
 * Projection for {@link org.backend.user.entity.UserConnection}
 */
public interface UserConnectionProjection {
    Boolean getIsCloseFriend();

    Instant getAddedDate();

    String getFollowerUserName();
    String getFollowerFirstName();
    String getFollowerLastName();

    String getFollowingUserName();
    String getFollowingFirstName();
    String getFollowingLastName();
}