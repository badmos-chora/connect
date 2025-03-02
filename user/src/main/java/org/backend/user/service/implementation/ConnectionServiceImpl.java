package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.entity.UserConnection;
import org.backend.user.enums.Status;
import org.backend.user.enums.UserConnectionType;
import org.backend.user.exception.BusinessException;
import org.backend.user.projections.UserConnectionProjection;
import org.backend.user.repository.interfaces.UserConnectionRepository;
import org.backend.user.service.interfaces.ConnectionService;
import org.backend.user.utils.ServiceResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private UserConnectionRepository userConnectionRepository;

    @Override
    @Transactional
    public void newConnection(Long senderUserId, Long receiverUserId, UserConnectionType type) throws Exception {
            UserConnectionId userConnectionId = new UserConnectionId();
            userConnectionId.setFollowerId(senderUserId);
            userConnectionId.setFollowingId(receiverUserId);
            UserConnection.UserConnectionBuilder userConnectionBuilder = UserConnection.builder();
            userConnectionBuilder.userConnectionId(userConnectionId)
                    .connectionType(type);
            userConnectionRepository.saveAndFlush(userConnectionBuilder.build());
    }


    @Override
    public ServiceResponse<UserConnectionProjection> getUserFollowingAndFollowerList(Long userId) {
        ServiceResponse.ServiceResponseBuilder<UserConnectionProjection> response = ServiceResponse.builder();
        try {
            List<String> projectionRules = List.of("isCloseFriend", "addedDate", "followerUserName", "followerFirstName", "followerLastName", "followingUserName", "followingFirstName", "followingLastName");
            List<UserConnectionProjection> followingList = userConnectionRepository.findBy(UserConnectionRepository.Specs.connectionType(UserConnectionType.FOLLOWING).and(UserConnectionRepository.Specs.followerIdEquals(userId)), q->
                    q.as(UserConnectionProjection.class)
                            .project(projectionRules)
                            .all()
            );

            List<UserConnectionProjection> followerList = userConnectionRepository.findBy(UserConnectionRepository.Specs.connectionType(UserConnectionType.FOLLOWING).and(UserConnectionRepository.Specs.followingIdEquals(userId)), q->
                    q.as(UserConnectionProjection.class)
                            .project(projectionRules)
                            .all()
            );
            Map<String,List<UserConnectionProjection>> data = new HashMap<>();
                    data.put("following", followingList);
                    data.put("follower", followerList);

            response.status(Status.OK).map(data);

        } catch (Exception e) {
            log.error("Error while getting user following and follower list: " , e);
            response.status(Status.ERROR).message("Error while getting user following and follower list");
        }

        return response.build();
    }


    @Override
    public void updateConnectionsStatus(Long senderUserId, Long receiverUserId, UserConnectionType connectionType) throws BusinessException {
        int recordsAffected = userConnectionRepository.updateConnectionsStatus(senderUserId, receiverUserId, connectionType);
        if(recordsAffected == 0) throw new BusinessException("No such connection found to update");
    }

    @Override
    public boolean existsConnectionWithType(Long initiator, Long otherParty, List<UserConnectionType> connectionType, boolean bidirectionalSpecification) {
        List<String> connectionTypeString = connectionType.stream().map(Enum::name).toList();
        Specification<UserConnection> conditions =UserConnectionRepository.Specs.followerIdEquals(initiator).and(UserConnectionRepository.Specs.followingIdEquals(otherParty));
        if(bidirectionalSpecification) conditions = conditions.or(UserConnectionRepository.Specs.followingIdEquals(initiator).and(UserConnectionRepository.Specs.followerIdEquals(otherParty)));
        return userConnectionRepository.exists(conditions.and(UserConnectionRepository.Specs.connectionTypeIn(connectionTypeString)));
    }

    @Override
    public long deleteUserConnection(Long initiatorId, Long otherPartyId) {
        return userConnectionRepository.delete(UserConnectionRepository.Specs.followerIdEquals(initiatorId).and(UserConnectionRepository.Specs.followingIdEquals(otherPartyId)));
    }
}
