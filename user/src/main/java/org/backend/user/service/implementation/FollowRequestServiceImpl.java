package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.entity.FollowRequest;
import org.backend.user.entity.User;
import org.backend.user.enums.FollowRequestStatus;
import org.backend.user.enums.Status;
import org.backend.user.projections.FollowRequestProjection;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.repository.interfaces.FollowRequestRepository;
import org.backend.user.repository.interfaces.UserRepository;
import org.backend.user.service.interfaces.ConnectionService;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.SecurityUtils;
import org.backend.user.utils.ServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FollowRequestServiceImpl implements FollowRequestService {
    private static final Logger LOGGER = Logger.getLogger(FollowRequestServiceImpl.class.getName());
    private FollowRequestRepository followRequestRepository;
    private UserRepository userRepository;
    private ConnectionService connectionService;

    @Override
    public Set<FollowRequestDto> getFollowRequests() {
        Long presentUserId = SecurityUtils.getCurrentUserId();
        List<String> project = Arrays.asList("id", "senderUserId", "senderUserUserName", "senderUserFirstName", "senderUserLastName", "receiverUserId",  "receiverUserUserName","receiverUserFirstName","receiverUserLastName", "status", "sentAt");
        List<FollowRequestProjection> followRequestProjection = followRequestRepository.findBy(FollowRequestRepository.Specs.receiverId(presentUserId).and(FollowRequestRepository.Specs.statusIn(FollowRequestStatus.PENDING)),q -> q.as(FollowRequestProjection.class).project(project).all());
        Set<FollowRequestDto> followRequestDtoList = new LinkedHashSet<>();
        for (FollowRequestProjection followRequest : followRequestProjection) {

            FollowRequestDto.FollowRequestDtoBuilder followRequestDtoBuilder = FollowRequestDto.builder();
            followRequestDtoBuilder.id(followRequest.getId())
                    .senderUserId(followRequest.getSenderUserId())
                    .senderUserName(followRequest.getSenderUserUserName())
                    .senderFullName(followRequest.getSenderFullName())
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
    @Transactional
    public ServiceResponse<?> newFollowRequest(String userName) {
        ServiceResponse.ServiceResponseBuilder<Object> serviceResponse = ServiceResponse.builder();
        try {
            FollowRequest.FollowRequestBuilder followRequestBuilder = FollowRequest.builder();
            User receiverUser = userRepository.findUserByUserNameIgnoreCase(userName);
            if (receiverUser == null) throw new Exception("No such user found");
            User senderUser = userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
            if(receiverUser.getIsPrivate()){
                followRequestBuilder.senderUser(senderUser)
                        .receiverUser(receiverUser)
                        .sentAt(Instant.now())
                        .status(FollowRequestStatus.PENDING);
                followRequestRepository.save(followRequestBuilder.build());
                serviceResponse
                        .status(Status.OK)
                        .message("Follow request sent successfully");
            }
            else {
                ServiceResponse<?> response = connectionService.newConnection(senderUser.getId(),receiverUser.getId());
                serviceResponse.status(response.getStatus()).message(response.getMessage());

                if(response.getStatus().equals(Status.ERROR))
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

        } catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to send follow request");
            LOGGER.severe("Error while sending follow request: " + e.getMessage());
        }
        return serviceResponse.build();
    }

    @Override
    public ServiceResponse<?> cancelFollowRequest(String userName) {
        ServiceResponse.ServiceResponseBuilder<Object> serviceResponse = ServiceResponse.builder();
        try {
            Long senderUserId = userRepository.findBy(UserRepository.Specs.userName(userName), q -> q.as(UserInfoProjection.class).project("id").first()).map(UserInfoProjection::getId).orElseThrow(()-> new Exception("No such user "+userName));
            Long receiverUserId = SecurityUtils.getCurrentUserId();
            int recordsAffected = followRequestRepository.updateFollowRequestStatus(receiverUserId, senderUserId, FollowRequestStatus.REJECTED);
            if(recordsAffected ==0) throw new Exception("No such follow request found for user "+userName+ " and current user "+senderUserId);
            serviceResponse
                    .status(Status.OK)
                    .message("Follow request cancelled successfully");
        } catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to cancel follow request");
            LOGGER.severe("Error while cancelling follow request: " + e);
        }
        return serviceResponse.build();
    }

    @Override
    @Transactional
    public ServiceResponse<?> acceptFollowRequest(String userName) {
        ServiceResponse.ServiceResponseBuilder<Object> serviceResponse = ServiceResponse.builder();
        try{
            Long senderUserId = userRepository.findBy(UserRepository.Specs.userName(userName), q -> q.as(UserInfoProjection.class).project("id").first()).map(UserInfoProjection::getId).orElseThrow(()-> new Exception("No such user "+userName));
            Long receiverUserId = SecurityUtils.getCurrentUserId();
            int recordsAffected = followRequestRepository.updateFollowRequestStatus(receiverUserId, senderUserId, FollowRequestStatus.ACCEPTED);
            if(recordsAffected ==0) throw new Exception("No such follow request found for user "+userName+ " and current user "+senderUserId);

            ServiceResponse<?> response =connectionService.newConnection(senderUserId,receiverUserId);
            serviceResponse.status(response.getStatus()).message(response.getMessage());
            if(response.getStatus().equals(Status.ERROR))
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to accept follow request");
            LOGGER.severe("Error while accepting follow request: " + e);
        }
        return serviceResponse.build();
    }
}
