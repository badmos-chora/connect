package org.backend.user.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.backend.user.dto.LoginDto;
import org.backend.user.dto.UserDto;
import org.backend.user.enums.Status;
import org.backend.user.projections.UserInfoProjection;
import org.backend.user.service.interfaces.AccountServices;
import org.backend.user.utils.ServiceResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private AccountServices accountServices;

    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto) {
        return accountServices.register(userDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody LoginDto loginDto) {
        ServiceResponse<?> response = accountServices.authenticate(loginDto.username(),loginDto.password());
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
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

    @GetMapping("/block/{userName}")
    public ResponseEntity<?> blockUserName(@NotNull @PathVariable String userName) {
        ServiceResponse<?> response= accountServices.blockUserName(userName);
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }

    @GetMapping("/block")
    public ResponseEntity<?> blockedUsersList() {
        ServiceResponse<?> response= accountServices.blockedUsersList();
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }


    @DeleteMapping("/block/{userName}")
    public ResponseEntity<?> unblockUserName(@NotNull @PathVariable String userName) {
        ServiceResponse<?> response= accountServices.unblockByUserName(userName);
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }

    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> userProfilePicture( @RequestPart("picture") MultipartFile file) {
        ServiceResponse<?> response= accountServices.userProfilePicture(file);
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }

    @DeleteMapping(value = "/profile-picture")
    public ResponseEntity<?> removeProfilePicture() {
        ServiceResponse<?> response= accountServices.removeProfilePicture();
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }


}
