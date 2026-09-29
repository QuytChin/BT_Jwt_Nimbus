package com.hcmute.jwtnimbus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entity User bám theo ví dụ trong slide JWT:
 * id, fullName, email, images, password, createdAt, updatedAt và UserDetails.
 */
@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Trường images có trong Entity của ví dụ bài giảng.
    // DEFAULT giúp Hibernate cập nhật được cả database đã chạy bản project cũ.
    @Column(nullable = false, columnDefinition = "nvarchar(50) default 'u1.jpg'")
    private String images = "u1.jpg";

    @JsonIgnore
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String role = "USER";

    // Dùng để demo trường hợp AccountStatusException/LockedException ở bước Exception.
    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean accountLocked = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (images == null || images.isBlank()) images = "u1.jpg";
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (images == null || images.isBlank()) images = "u1.jpg";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !accountLocked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
