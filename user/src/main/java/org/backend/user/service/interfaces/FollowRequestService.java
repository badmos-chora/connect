package org.backend.user.service.interfaces;

import org.backend.user.dto.FollowRequestDto;

import java.util.Set;

public interface FollowRequestService {
    Set<FollowRequestDto> getFollowRequests();
}
