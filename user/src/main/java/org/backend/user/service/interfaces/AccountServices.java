package org.backend.user.service.interfaces;

import org.backend.user.dto.UserDto;

public interface AccountServices {
    String register(UserDto userDto);
}
