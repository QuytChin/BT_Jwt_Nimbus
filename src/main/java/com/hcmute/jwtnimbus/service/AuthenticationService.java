package com.hcmute.jwtnimbus.service;

import com.hcmute.jwtnimbus.dto.LoginUserModel;
import com.hcmute.jwtnimbus.dto.RegisterUserModel;
import com.hcmute.jwtnimbus.entity.User;
import com.hcmute.jwtnimbus.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signup(RegisterUserModel input) {
        String email = input.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại");
        }
        User user = new User();
        user.setFullName(input.getFullName().trim());
        user.setEmail(email);
        user.setImages("u1.jpg");
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User authenticate(LoginUserModel input) {
        String email = input.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, input.getPassword()));
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }
}
