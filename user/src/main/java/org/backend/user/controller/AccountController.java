package org.backend.user.controller;

import lombok.AllArgsConstructor;
import org.backend.user.dto.UserDto;
import org.backend.user.service.interfaces.AccountServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private AccountServices accountServices;


    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto) {
        return accountServices.register(userDto);
    }

}
