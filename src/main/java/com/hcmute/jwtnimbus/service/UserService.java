package com.hcmute.jwtnimbus.service;

import com.hcmute.jwtnimbus.entity.User;
import com.hcmute.jwtnimbus.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }
    public List<User> allUsers() { return userRepository.findAll(); }
}
