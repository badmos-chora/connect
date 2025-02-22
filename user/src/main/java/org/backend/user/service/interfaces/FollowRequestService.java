package org.backend.user.service.interfaces;

import org.backend.user.dto.FollowRequestDto;
import org.backend.user.utils.ServiceResponse;

import java.util.Set;

public interface FollowRequestService {
    Set<FollowRequestDto> getFollowRequests();
    ServiceResponse<?> newFollowRequest(String userName);
}
