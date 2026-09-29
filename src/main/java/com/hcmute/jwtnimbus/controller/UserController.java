package com.hcmute.jwtnimbus.controller;

import com.hcmute.jwtnimbus.dto.UserResponse;
import com.hcmute.jwtnimbus.entity.User;
import com.hcmute.jwtnimbus.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> authenticatedUser(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(UserResponse.from(currentUser));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> allUsers() {
        return ResponseEntity.ok(userService.allUsers().stream().map(UserResponse::from).toList());
    }
}
