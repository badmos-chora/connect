package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.user.dto.UserDto;
import org.backend.user.dto.UserInfoDTO;
import org.backend.user.entity.User;
import org.backend.user.enums.FollowRequestStatus;
import org.backend.user.enums.Status;
import org.backend.user.enums.UserConnectionType;
import org.backend.user.exception.BusinessException;
import org.backend.user.projections.UserConnectionProjection;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.repository.interfaces.UserRepository;
import org.backend.user.service.interfaces.AccountServices;
import org.backend.user.service.interfaces.ConnectionService;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.SecurityUtils;
import org.backend.user.utils.ServiceResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServicesImpl implements AccountServices {

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private ConnectionService connectionService;
    private FollowRequestService followRequestService;


    @Override
    public String register(UserDto userDto) {
        User.UserBuilder userBuilder = User.builder();
        userBuilder.firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .userName(userDto.userName())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .isEnabled(true)
                .isLocked(false);
        userRepository.save(userBuilder.build());
        return "User registered successfully";
    }

    @Override
    public UserInfoProjection profile() {
        Long userID = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userID, UserInfoProjection.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<?> profileForUserName(String userName) {
        ServiceResponse.ServiceResponseBuilder<UserInfoDTO> responseBuilder = ServiceResponse.builder();
        try {
            Long loggedInUserId = SecurityUtils.getCurrentUserId();
            UserInfoProjection user = userRepository.findUserByUserNameIgnoreCase(userName, UserInfoProjection.class);
            boolean badRequest = user == null || user.getId()== null || !user.isActive();
            if (!badRequest) badRequest = connectionService.existsConnectionWithType(user.getId(), loggedInUserId, Collections.singletonList(UserConnectionType.BLOCKED), true);
            if(badRequest) throw new BusinessException("No active user found with username: "+userName);
            UserInfoDTO.UserInfoDTOBuilder userInfoDTOBuilder = UserInfoDTO.builder();
            userInfoDTOBuilder.isPrivate(user.getIsPrivate())
                    .fullName(user.getFirstName() + " " + user.getLastName())
                    .userName(user.getUserName());
            ServiceResponse<UserConnectionProjection> response = connectionService.getUserFollowingAndFollowerList(user.getId());
            if(response.getStatus().equals(Status.ERROR)) return response;
            Map<String, List<UserConnectionProjection>> map = response.getMap();
            List<UserConnectionProjection> followingList = map.get("following");
            List<UserConnectionProjection> followerList = map.get("follower");
            userInfoDTOBuilder
                    .followingCount((long) followingList.size())
                    .followerCount((long) followerList.size());

            if(!user.getIsPrivate()){
                userInfoDTOBuilder.followerList(followerList)
                        .followingList(followingList);
            }

            responseBuilder
                    .status(Status.OK)
                    .data(userInfoDTOBuilder.build())
                    .message("User profile fetched successfully");
        } catch (BusinessException e) {
            log.warn(e.toString());
            responseBuilder
                    .status(Status.BAD_REQUEST)
                    .message(e.getMessage());
        }
        catch (Exception e) {
            log.error(e.toString());
            responseBuilder
                    .status(Status.ERROR)
                    .message("Error occurred while fetching user profile for username: "+userName);
        }
        return responseBuilder.build();
    }

    @Override
    @Transactional
    public ServiceResponse<?> blockUserName(String userName) {
        ServiceResponse.ServiceResponseBuilder<?> serviceResponse = ServiceResponse.builder();
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            UserInfoProjection userToBlock = userRepository.findUserByUserNameIgnoreCase(userName, UserInfoProjection.class);
            if(userToBlock == null || userToBlock.getId().equals(userId)) throw new BusinessException("No such user found with username: "+userName);

            if(followRequestService.checkFollowRequestExists(userId, userToBlock.getId(), FollowRequestStatus.PENDING))
                 followRequestService.updateFollowRequestStatus(userId,userToBlock.getId(), FollowRequestStatus.REJECTED);

            if(followRequestService.checkFollowRequestExists(userToBlock.getId(), userId, FollowRequestStatus.PENDING))
                followRequestService.updateFollowRequestStatus(userToBlock.getId(), userId, FollowRequestStatus.REJECTED);

            if(connectionService.existsConnectionWithType(userId, userToBlock.getId(), List.of(UserConnectionType.FOLLOWING, UserConnectionType.BLOCKED), false))
                connectionService.updateConnectionsStatus(userId, userToBlock.getId(), UserConnectionType.BLOCKED);
            else
                 connectionService.newConnection(userId, userToBlock.getId(), UserConnectionType.BLOCKED);

            serviceResponse.message("User blocked successfully").status(Status.OK);
        } catch (BusinessException e) {
            log.warn(e.getMessage());
            serviceResponse.status(Status.BAD_REQUEST).message(e.getMessage());
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error occurred while blocking user with username:{} ",userName, e);
            serviceResponse.status(Status.ERROR).message("Error occurred while blocking user with username");
        }

        return serviceResponse.build();
    }
}
