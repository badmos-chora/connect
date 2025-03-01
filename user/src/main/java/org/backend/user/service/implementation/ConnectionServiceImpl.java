package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.entity.UserConnection;
import org.backend.user.enums.Status;
import org.backend.user.enums.UserConnectionType;
import org.backend.user.projections.UserConnectionProjection;
import org.backend.user.repository.interfaces.UserConnectionRepository;
import org.backend.user.service.interfaces.ConnectionService;
import org.backend.user.utils.ServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private static final Logger LOGGER = Logger.getLogger(ConnectionServiceImpl.class.getName());
    private final DataSource dataSource;
    private UserConnectionRepository userConnectionRepository;

    @Override
    @Transactional
    public ServiceResponse<?> newConnection(Long senderUserId, Long receiverUserId) {
        ServiceResponse.ServiceResponseBuilder<?> response = ServiceResponse.builder();
        try {
            UserConnectionId userConnectionId = new UserConnectionId();
            userConnectionId.setFollowerId(senderUserId);
            userConnectionId.setFollowingId(receiverUserId);
            UserConnection.UserConnectionBuilder userConnectionBuilder = UserConnection.builder();
            userConnectionBuilder.userConnectionId(userConnectionId);
            userConnectionRepository.saveAndFlush(userConnectionBuilder.build());
            response.status(Status.OK).message("Connection created successfully");
        } catch (Exception e) {
            LOGGER.severe("Error while creating connection: " + e);
            response.status(Status.ERROR).message("Error while creating connection");
        }
        return response.build();
    }


    @Override
    public ServiceResponse<UserConnectionProjection> getUserFollowingAndFollowerList(Long userId) {
        ServiceResponse.ServiceResponseBuilder<UserConnectionProjection> response = ServiceResponse.builder();
        try {
            List<String> projectionRules = List.of("isCloseFriend", "addedDate", "followerUserName", "followerFirstName", "followerLastName", "followingUserName", "followingFirstName", "followingLastName");
            List<UserConnectionProjection> followingList = userConnectionRepository.findBy(UserConnectionRepository.Specs.connectionType(UserConnectionType.FOLLOWING).and(UserConnectionRepository.Specs.getUserFollowings(userId)), q->
                    q.as(UserConnectionProjection.class)
                            .project(projectionRules)
                            .all()
            );

            List<UserConnectionProjection> followerList = userConnectionRepository.findBy(UserConnectionRepository.Specs.connectionType(UserConnectionType.FOLLOWING).and(UserConnectionRepository.Specs.getUserFollowers(userId)), q->
                    q.as(UserConnectionProjection.class)
                            .project(projectionRules)
                            .all()
            );
            Map<String,List<UserConnectionProjection>> data = new HashMap<>();
                    data.put("following", followingList);
                    data.put("follower", followerList);

            response.status(Status.OK).map(data);

        } catch (Exception e) {
            LOGGER.severe("Error while getting user following and follower list: " + e);
            response.status(Status.ERROR).message("Error while getting user following and follower list");
        }

        return response.build();
    }
}
