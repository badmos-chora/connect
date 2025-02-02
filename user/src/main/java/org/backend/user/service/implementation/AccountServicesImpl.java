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
        User newUser = new User();
        newUser.setUserName(userDto.userName());
        newUser.setFirstName(userDto.firstName());
        newUser.setLastName(userDto.lastName());
        newUser.setEmail(userDto.email());newUser.setPassword(passwordEncoder.encode(userDto.password()));
        newUser.setIsEnabled(true);
        newUser.setIsLocked(false);
        userRepository.save(newUser);
        return "User registered successfully";
    }
}
