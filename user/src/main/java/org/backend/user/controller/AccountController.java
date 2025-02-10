package org.backend.user.controller;

import lombok.AllArgsConstructor;
import org.backend.user.dto.UserDto;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.service.interfaces.AccountServices;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private AccountServices accountServices;

    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto) {
        return accountServices.register(userDto);
    }

    @GetMapping("/profile")
    public UserInfoProjection profile() {
        return accountServices.profile();
    }
}
