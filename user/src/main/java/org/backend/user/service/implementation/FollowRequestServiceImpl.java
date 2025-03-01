package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.entity.FollowRequest;
import org.backend.user.entity.User;
import org.backend.user.enums.FollowRequestStatus;
import org.backend.user.enums.Status;
import org.backend.user.exception.BusinessException;
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
@Slf4j
public class FollowRequestServiceImpl implements FollowRequestService {
    private FollowRequestRepository followRequestRepository;
    private UserRepository userRepository;
    private ConnectionService connectionService;

    @Override
    public Set<FollowRequestDto> getFollowRequests() {
        Long presentUserId = SecurityUtils.getCurrentUserId();
        List<String> project = Arrays.asList("id", "senderUserId", "senderUserUserName", "senderUserFirstName", "senderUserLastName", "receiverUserId",  "receiverUserUserName","receiverUserFirstName","receiverUserLastName", "status", "sentAt");
        List<FollowRequestProjection> followRequestProjection = followRequestRepository.findBy(FollowRequestRepository.Specs.receiverId(presentUserId).and(FollowRequestRepository.Specs.statusIn(FollowRequestStatus.PENDING)).and(FollowRequestRepository.Specs.isAccountActive()),q -> q.as(FollowRequestProjection.class).project(project).all());
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
            User receiverUser = userRepository.findUserByUserNameIgnoreCase(userName, User.class);
            User senderUser = userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
            if (receiverUser == null || receiverUser.getIsLocked() || !receiverUser.getIsEnabled() || receiverUser.getId().equals(senderUser.getId())) throw new BusinessException("No such user found");
            boolean alreadyExist = followRequestRepository.exists(FollowRequestRepository.Specs.receiverId(receiverUser.getId()).and(FollowRequestRepository.Specs.senderId(senderUser.getId())).and(FollowRequestRepository.Specs.statusIn(FollowRequestStatus.PENDING)));
            if(alreadyExist) throw new BusinessException("Follow request already sent to user "+userName);

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

        } catch (BusinessException e) {
            serviceResponse.status(Status.BAD_REQUEST)
                    .message(e.getMessage());
            log.warn(e.getMessage());
        }
        catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to send follow request");
            log.error("Error while sending follow request: " , e);
        }
        return serviceResponse.build();
    }

    @Override
    public ServiceResponse<?> cancelFollowRequest(String userName) {
        ServiceResponse.ServiceResponseBuilder<Object> serviceResponse = ServiceResponse.builder();
        try {
            Long senderUserId = userRepository.findBy(UserRepository.Specs.userName(userName), q -> q.as(UserInfoProjection.class).project("id").first()).map(UserInfoProjection::getId).orElseThrow(()-> new BusinessException("No such user "+userName));
            Long receiverUserId = SecurityUtils.getCurrentUserId();
            int recordsAffected = followRequestRepository.updateFollowRequestStatus(receiverUserId, senderUserId, FollowRequestStatus.REJECTED);
            if(recordsAffected ==0) throw new BusinessException("No such follow request found for user "+userName+ " and current user "+senderUserId);
            serviceResponse
                    .status(Status.OK)
                    .message("Follow request cancelled successfully");
        } catch (BusinessException e) {
            serviceResponse.status(Status.BAD_REQUEST)
                    .message(e.getMessage());
            log.warn(e.getMessage());
        }
        catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to cancel follow request");
            log.error("Error while cancelling follow request: ", e);
        }
        return serviceResponse.build();
    }

    @Override
    @Transactional
    public ServiceResponse<?> acceptFollowRequest(String userName) {
        ServiceResponse.ServiceResponseBuilder<Object> serviceResponse = ServiceResponse.builder();
        try{
            Long senderUserId = userRepository.findBy(UserRepository.Specs.userName(userName), q -> q.as(UserInfoProjection.class).project("id").first()).map(UserInfoProjection::getId).orElseThrow(()-> new BusinessException("No such user "+userName));
            Long receiverUserId = SecurityUtils.getCurrentUserId();
            int recordsAffected = followRequestRepository.updateFollowRequestStatus(receiverUserId, senderUserId, FollowRequestStatus.ACCEPTED);
            if(recordsAffected ==0) throw new BusinessException("No such follow request found for user "+userName+ " and current user "+senderUserId);

            ServiceResponse<?> response =connectionService.newConnection(senderUserId,receiverUserId);
            serviceResponse.status(response.getStatus()).message(response.getMessage());
            if(response.getStatus().equals(Status.ERROR))
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } catch (BusinessException e) {
            serviceResponse.status(Status.BAD_REQUEST)
                    .message(e.getMessage());
            log.warn(e.getMessage());
        }
        catch (Exception e) {
            serviceResponse.status(Status.ERROR)
                    .message("Failed to accept follow request");
            log.error("Error while accepting follow request: ", e);
        }
        return serviceResponse.build();
    }
}
