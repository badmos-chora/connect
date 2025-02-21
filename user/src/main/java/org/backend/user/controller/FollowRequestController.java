package org.backend.user.controller;

import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.service.interfaces.FollowRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

}
