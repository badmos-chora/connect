package org.backend.user.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "hiii";
    }

    @GetMapping("/hi")
    public String hi() {
        return "hello";
    }

    @GetMapping("/auth")
    public String auth() {
        return "authenticated";
    }

}
