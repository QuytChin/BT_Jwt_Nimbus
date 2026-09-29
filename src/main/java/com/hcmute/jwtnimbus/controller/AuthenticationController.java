package com.hcmute.jwtnimbus.controller;

import com.hcmute.jwtnimbus.dto.*;
import com.hcmute.jwtnimbus.entity.User;
import com.hcmute.jwtnimbus.service.AuthenticationService;
import com.hcmute.jwtnimbus.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserModel registerUser) {
        User registered = authenticationService.signup(registerUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(registered));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserModel loginUser) {
        User authenticatedUser = authenticationService.authenticate(loginUser);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok(new LoginResponse(jwtToken, jwtService.getExpirationTime(), UserResponse.from(authenticatedUser)));
    }
}
