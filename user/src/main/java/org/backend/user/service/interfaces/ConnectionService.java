package org.backend.user.service.interfaces;

import org.backend.user.projections.UserConnectionProjection;
import org.backend.user.utils.ServiceResponse;

public interface ConnectionService {
    ServiceResponse<?> newConnection(Long senderUserId, Long receiverUserId);

    ServiceResponse<UserConnectionProjection> getUserFollowingAndFollowerList(Long id);
}
