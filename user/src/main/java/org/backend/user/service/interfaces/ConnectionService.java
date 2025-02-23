package org.backend.user.service.interfaces;

import org.backend.user.utils.ServiceResponse;

public interface ConnectionService {
    ServiceResponse<?> newConnection(Long senderUserId, Long receiverUserId);
}
