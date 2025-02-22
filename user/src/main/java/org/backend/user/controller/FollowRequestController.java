package org.backend.user.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.enums.Status;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/fr")
@AllArgsConstructor
public class FollowRequestController {

    private final FollowRequestService followRequestService;

    @GetMapping(path = {""," ","/"}, produces = "application/json")
    public Set<FollowRequestDto> getFollowRequests() {
        return followRequestService.getFollowRequests();
    }

    @GetMapping(path = "/add/{userName}", produces = "application/json")
    public ResponseEntity<?> newFollowRequest(@PathVariable @NotNull String userName) {
         ServiceResponse<?> response = followRequestService.newFollowRequest(userName);
         if (response.getStatus().equals(Status.OK)) {
             return ResponseEntity.ok(response);
         } else {
             return ResponseEntity.internalServerError().body(response.getMessage());
         }
    }

    @DeleteMapping(path = "/cancel/{userName}", produces = "application/json")
    public ResponseEntity<?> cancelFollowRequest(@PathVariable @NotNull String userName) {
        ServiceResponse<?> response = followRequestService.cancelFollowRequest(userName);
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }

}
