package org.backend.user.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.backend.user.dto.UserDto;
import org.backend.user.enums.Status;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.service.interfaces.AccountServices;
import org.backend.user.utils.ServiceResponse;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/profile/{userName}")
    public ResponseEntity<?> profileForUserName(@NotNull @PathVariable String userName) {
        ServiceResponse<?> response= accountServices.profileForUserName(userName);
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }
}
