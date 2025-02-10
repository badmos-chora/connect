package org.backend.user.service.interfaces;

import org.backend.user.dto.UserDto;
import org.backend.user.projections.UserInfoProjection;

public interface AccountServices {
    String register(UserDto userDto);

    UserInfoProjection profile();
}
