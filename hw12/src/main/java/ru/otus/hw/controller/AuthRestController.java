package ru.otus.hw.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.otus.hw.dto.CurrentUserDto;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    
    @GetMapping("/me")
    public CurrentUserDto getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            return new CurrentUserDto(true, user.getUsername());
        }
        return new CurrentUserDto(false, null);
    }
}