package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.entity.FollowRequest;
import org.backend.user.entity.User;
import org.backend.user.enums.FollowRequestStatus;
import org.backend.user.enums.Status;
import org.backend.user.projections.FollowRequestProjection;
import org.backend.user.repository.interfaces.FollowRequestRepository;
import org.backend.user.repository.interfaces.UserRepository;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.SecurityUtils;
import org.backend.user.utils.ServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class FollowRequestServiceImpl implements FollowRequestService {

    private FollowRequestRepository followRequestRepository;
    private UserRepository userRepository;

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

    @Override
    public ServiceResponse<?> newFollowRequest(String userName) {
        ServiceResponse.ServiceResponseBuilder<Object> serviceResponse = ServiceResponse.builder();
        try {
            FollowRequest.FollowRequestBuilder followRequestBuilder = FollowRequest.builder();
            User receiverUser = userRepository.findUserByUserNameIgnoreCase(userName);
            User senderUser = userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
            followRequestBuilder.senderUser(senderUser)
                    .receiverUser(receiverUser)
                    .sentAt(Instant.now())
                    .status(FollowRequestStatus.PENDING);
            followRequestRepository.save(followRequestBuilder.build());
            serviceResponse
                    .status(Status.OK)
                    .message("Follow request sent successfully");
        } catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to send follow request");
            System.out.println(e.getMessage());
        }
        return serviceResponse.build();
    }
}
