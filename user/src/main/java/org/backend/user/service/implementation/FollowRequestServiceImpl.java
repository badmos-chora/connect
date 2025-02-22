package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.entity.FollowRequest;
import org.backend.user.entity.User;
import org.backend.user.projections.FollowRequestProjection;
import org.backend.user.repository.interfaces.FollowRequestRepository;
import org.backend.user.repository.interfaces.UserRepository;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class FollowRequestServiceImpl implements FollowRequestService {

    private FollowRequestRepository followRequestRepository;

    @Override
    public Set<FollowRequestDto> getFollowRequests() {
        Long presentUserId = SecurityUtils.getCurrentUserId();
        List<String> project = Arrays.asList("id", "senderUserId", "senderUserUserName", "senderUserFirstName", "senderUserLastName", "receiverUserId",  "receiverUserUserName","receiverUserFirstName","receiverUserLastName", "status", "sentAt");
        List<FollowRequestProjection> followRequestProjection = followRequestRepository.findBy(FollowRequestRepository.Specs.receiverId(presentUserId),q -> q.as(FollowRequestProjection.class).project(project).all());
        Set<FollowRequestDto> followRequestDtoList = new LinkedHashSet<>();
        for (FollowRequestProjection followRequest : followRequestProjection) {

            FollowRequestDto.FollowRequestDtoBuilder followRequestDtoBuilder = FollowRequestDto.builder();
            followRequestDtoBuilder.id(followRequest.getId())
                    .senderUserId(followRequest.getSenderUserId())
                    .senderUserName(followRequest.getSenderUserUserName())
                    .senderFullName(followRequest.getReceiverFullName())
                    .receiverUserId(followRequest.getReceiverUserId())
                    .receiverUserName(followRequest.getReceiverUserUserName())
                    .receiverFullName(followRequest.getReceiverFullName())
                    .status(followRequest.getStatus()) ;


            Duration duration = Duration.between(followRequest.getSentAt(), Instant.now());
            if (duration.toDays() > 0) {
                followRequestDtoBuilder.sentAt(duration.toDays() + " days ago");
            } else if (duration.toHours() > 0) {
                followRequestDtoBuilder.sentAt(duration.toHours() + " hours ago");
            } else if (duration.toMinutes() > 0) {
                followRequestDtoBuilder.sentAt(duration.toMinutes() + " minutes ago");
            } else {
                followRequestDtoBuilder.sentAt(duration.toSeconds() + " seconds ago");
            }
            followRequestDtoList.add(followRequestDtoBuilder.build());
        }
        return followRequestDtoList;
    }
}
