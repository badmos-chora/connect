package org.backend.user.security;

import org.backend.user.entity.User;
import org.backend.user.repository.interfaces.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class CustomUserDetailsService implements UserDetailsService {

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserNameIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: "+username);
        }else
            return new CustomUserDetails(user);
    }
}
