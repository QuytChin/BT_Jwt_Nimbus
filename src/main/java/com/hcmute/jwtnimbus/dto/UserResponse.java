package com.hcmute.jwtnimbus.dto;

import com.hcmute.jwtnimbus.entity.User;
import java.time.LocalDateTime;

public class UserResponse {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String images;
    private final String role;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String fullName, String email, String images, String role, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.images = images;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(), user.getFullName(), user.getEmail(), user.getImages(), user.getRole(), user.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getImages() { return images; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
