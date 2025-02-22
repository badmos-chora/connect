package org.backend.user.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.enums.Status;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
             return ResponseEntity.badRequest().body(response.getMessage());
         }
    }
}
