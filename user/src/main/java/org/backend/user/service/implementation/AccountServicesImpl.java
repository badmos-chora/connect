package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.dto.UserDto;
import org.backend.user.entity.User;
import org.backend.user.repository.UserRepository;
import org.backend.user.service.interfaces.AccountServices;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServicesImpl implements AccountServices {

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;


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
}
