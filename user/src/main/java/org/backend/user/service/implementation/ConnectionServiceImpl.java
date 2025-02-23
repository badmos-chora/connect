package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.entity.UserConnection;
import org.backend.user.enums.Status;
import org.backend.user.repository.interfaces.UserConnectionRepository;
import org.backend.user.service.interfaces.ConnectionService;
import org.backend.user.utils.ServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private static final Logger LOGGER = Logger.getLogger(ConnectionServiceImpl.class.getName());
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
}
