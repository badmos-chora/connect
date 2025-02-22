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
    Long getSenderUserId();
    String getSenderUserUserName();
    String getSenderUserFirstName();
    String getSenderUserLastName();
    default String getSenderFullName() {
        return getSenderUserFirstName().concat(" ").concat(getSenderUserLastName());
    }

     Long getReceiverUserId();
    String getReceiverUserUserName();
    String getReceiverUserFirstName();
    String getReceiverUserLastName();
    default String getReceiverFullName() {
        return getReceiverUserFirstName().concat(" ").concat(getReceiverUserLastName());
    }



}