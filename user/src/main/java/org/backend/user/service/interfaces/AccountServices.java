package org.backend.user.service.interfaces;

import jakarta.validation.constraints.NotNull;
import org.backend.user.dto.UserDto;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.utils.ServiceResponse;

public interface AccountServices {
    String register(UserDto userDto);

    UserInfoProjection profile();

    ServiceResponse<?> profileForUserName(@NotNull String userName);

    ServiceResponse<?> blockUserName(@NotNull String userName);

    ServiceResponse<?> unblockByUserName(@NotNull String userName);
}
