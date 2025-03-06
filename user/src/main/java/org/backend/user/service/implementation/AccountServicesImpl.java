package org.backend.user.service.implementation;

import lombok.RequiredArgsConstructor;
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
import org.backend.user.service.interfaces.FileServices;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.SecurityUtils;
import org.backend.user.utils.ServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServicesImpl implements AccountServices {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ConnectionService connectionService;
    private final FollowRequestService followRequestService;
    private final FileServices fileServices;

    @Value("${connect.storage.profile-pic}")
    private String PROFILE_PICTURE_PATH;


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
                    .userName(user.getUserName())
                    .profilePicture(user.getProfilePicture());
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

    /*
    Todo
     optimize block user flow we can reduce the dao call structure like we can check if user is already blocked or not before updating the pending friend requests
     */

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

            if(connectionService.existsConnectionWithType(userId, userToBlock.getId(), List.of(UserConnectionType.FOLLOWING, UserConnectionType.BLOCKED), false)) {
                if(connectionService.existsConnectionWithType(userId, userToBlock.getId(), Collections.singletonList(UserConnectionType.FOLLOWING), false))
                    connectionService.updateConnectionsStatus(userId, userToBlock.getId(), UserConnectionType.BLOCKED);
                else
                    throw new Exception("User already blocked with username: "+userName);
            }
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

    @Override
    public ServiceResponse<?> unblockByUserName(String userName) {
        ServiceResponse.ServiceResponseBuilder<?> serviceResponse = ServiceResponse.builder();
        try {
            Long loggedInUserId = SecurityUtils.getCurrentUserId();
            UserInfoProjection userToUnblock = userRepository.findUserByUserNameIgnoreCase(userName, UserInfoProjection.class);
            if(userToUnblock == null || userToUnblock.getId().equals(loggedInUserId) || !userToUnblock.isActive()) throw new BusinessException("No such user found with username: "+userName);
            if(connectionService.existsConnectionWithType(loggedInUserId, userToUnblock.getId(), Collections.singletonList(UserConnectionType.BLOCKED), false))
                connectionService.deleteUserConnection(loggedInUserId, userToUnblock.getId());
            else throw new BusinessException("User is not blocked");
            serviceResponse.message("User unblocked successfully").status(Status.OK);
        } catch (BusinessException e) {
            log.warn(e.getMessage());
            serviceResponse.status(Status.BAD_REQUEST).message(e.getMessage());
        } catch (Exception e) {
            log.error("Error while unblocking user with username: {}", userName, e);
            serviceResponse.status(Status.ERROR).message(e.getMessage());
        }
        return serviceResponse.build();
    }

    @Override
    public ServiceResponse<?> blockedUsersList() {
        ServiceResponse.ServiceResponseBuilder<List<UserConnectionProjection>> serviceResponse = ServiceResponse.builder();
        try {
            Long loggedInUserId = SecurityUtils.getCurrentUserId();
            List<UserConnectionProjection> blockedUsersList = connectionService.getUserListOfConnectionType(loggedInUserId, UserConnectionType.BLOCKED);
            serviceResponse.data(blockedUsersList).status(Status.OK);
        } catch (Exception e) {
            serviceResponse.status(Status.ERROR).message(e.getMessage());
            log.error("Error occurred while blocking user list", e);
        }
        return serviceResponse.build();
    }

    @Override
    public ServiceResponse<?> userProfilePicture(MultipartFile file) {
        ServiceResponse.ServiceResponseBuilder<?> serviceResponseBuilder = ServiceResponse.builder();
        try {
            if (file == null || file.isEmpty() || file.getContentType() == null) throw new BusinessException("File/ContentType is empty");
            Long loggedInUserId = SecurityUtils.getCurrentUserId();
            String fileType;
            if(file.getContentType().equals(MimeTypeUtils.IMAGE_JPEG_VALUE)) fileType = "jpg";
            else if(file.getContentType().equals(MimeTypeUtils.IMAGE_PNG_VALUE)) fileType = "png";
            else throw new BusinessException("Invalid file type");
            String path = fileServices.saveNewFile(file, loggedInUserId, PROFILE_PICTURE_PATH ,fileType);
            userRepository.updateUserProfilePicture(loggedInUserId, path);
            serviceResponseBuilder.status(Status.OK).message("User profile picture updated successfully");
        } catch (BusinessException e) {
            log.warn(e.getMessage());
            serviceResponseBuilder.status(Status.BAD_REQUEST).message(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error occurred while updating profile picture", e);
            serviceResponseBuilder.status(Status.ERROR).message("Error occurred while updating profile picture");
        }
        return serviceResponseBuilder.build();
    }

    @Override
    public ServiceResponse<?> removeProfilePicture() {
        ServiceResponse.ServiceResponseBuilder<?> serviceResponseBuilder = ServiceResponse.builder();
        try {
            Long loggedInUserId = SecurityUtils.getCurrentUserId();
            userRepository.updateUserProfilePicture(loggedInUserId, "");
            serviceResponseBuilder.status(Status.OK).message("User profile picture updated successfully");
        }
        catch (Exception e) {
            log.error("Error occurred while deleting profile picture", e);
            serviceResponseBuilder.status(Status.ERROR).message("Error occurred while deleting profile picture");
        }
        return serviceResponseBuilder.build();
    }


}
