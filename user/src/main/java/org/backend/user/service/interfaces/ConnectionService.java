package org.backend.user.service.interfaces;

import org.backend.user.enums.UserConnectionType;
import org.backend.user.exception.BusinessException;
import org.backend.user.projections.UserConnectionProjection;
import org.backend.user.utils.ServiceResponse;

import java.util.List;

public interface ConnectionService {
    void newConnection(Long senderUserId, Long receiverUserId, UserConnectionType following) throws Exception;

    ServiceResponse<UserConnectionProjection> getUserFollowingAndFollowerList(Long id);

    void updateConnectionsStatus(Long senderUserId, Long receiverUserId, UserConnectionType connectionType) throws BusinessException;
    boolean existsConnectionWithType(Long senderUserId, Long receiverUserId , List<UserConnectionType> connectionType, boolean bidirectional);

    long deleteUserConnection(Long initiatorId, Long otherPartyId);

    List<UserConnectionProjection> getUserListOfConnectionType(Long loggedInUserId, UserConnectionType userConnectionType);
}
