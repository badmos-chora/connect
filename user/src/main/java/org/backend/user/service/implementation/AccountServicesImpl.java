package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.user.dto.UserDto;
import org.backend.user.dto.UserInfoDTO;
import org.backend.user.entity.User;
import org.backend.user.enums.Status;
import org.backend.user.exception.BusinessException;
import org.backend.user.projections.UserConnectionProjection;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.repository.interfaces.UserRepository;
import org.backend.user.service.interfaces.AccountServices;
import org.backend.user.service.interfaces.ConnectionService;
import org.backend.user.utils.SecurityUtils;
import org.backend.user.utils.ServiceResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServicesImpl implements AccountServices {

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private ConnectionService connectionService;


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
            UserInfoProjection user = userRepository.findUserByUserNameIgnoreCase(userName, UserInfoProjection.class);
            if(user == null || !user.isActive()) throw new BusinessException("No active user found with username: "+userName);
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
}
