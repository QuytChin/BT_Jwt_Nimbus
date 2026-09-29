package com.hcmute.jwtnimbus.configuration;

import com.hcmute.jwtnimbus.entity.User;
import com.hcmute.jwtnimbus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Chuẩn hóa dữ liệu demo sau khi Hibernate đã tạo/cập nhật bảng:
 * - bảo đảm cột full_name là NVARCHAR để lưu tiếng Việt đúng;
 * - xóa tài khoản Admin Demo cũ;
 * - tạo/cập nhật tài khoản Trần Quyết Chiến để project import xong chạy ngay.
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.demo.enabled:true}")
    private boolean enabled;

    @Value("${app.demo.email:chien@gmail.com}")
    private String email;

    @Value("${app.demo.password:123456}")
    private String password;

    @Value("${app.demo.full-name:Tr\u1EA7n Quy\u1EBFt Chi\u1EBFn}")
    private String fullName;

    @Value("${app.demo.images:tran-quyet-chien.jpg}")
    private String images;

    public DemoDataInitializer(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;

        ensureUnicodeColumn();
        removeLegacyAdminDemo();
        upsertStudentAccount();
    }

    private void ensureUnicodeColumn() {
        // Các bản project cũ có thể đã tạo full_name dạng VARCHAR.
        // Chuyển sang NVARCHAR để dữ liệu tiếng Việt hiển thị đúng dấu.
        jdbcTemplate.execute("""
            IF COL_LENGTH('dbo.app_users', 'full_name') IS NOT NULL
            BEGIN
                ALTER TABLE dbo.app_users ALTER COLUMN full_name NVARCHAR(100) NOT NULL;
            END
            """);
    }

    private void removeLegacyAdminDemo() {
        userRepository.findByEmail("admin@gmail.com").ifPresent(userRepository::delete);
    }

    private void upsertStudentAccount() {
        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail).orElseGet(User::new);
        user.setFullName(fullName);
        user.setEmail(normalizedEmail);
        user.setImages(images);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");
        user.setAccountLocked(false);
        userRepository.save(user);

        System.out.println("[JWT NIMBUS] Tài khoản: " + normalizedEmail + " / " + password);
    }
}
