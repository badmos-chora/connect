package org.backend.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login page";
    }

    @GetMapping("/home")
    public String home() {
        return "home page";
    }

    @GetMapping("/hi")
    public String hi(HttpServletRequest request) {
        return "url: -> "+request.getHeader("X-Forwarded-Host");
    }

    @GetMapping("/auth")
    public String auth() {
        return "authenticated";
    }

    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "logout success";
    }

}
