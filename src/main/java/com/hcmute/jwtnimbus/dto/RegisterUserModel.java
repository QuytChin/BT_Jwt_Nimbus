package com.hcmute.jwtnimbus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterUserModel {
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
    @NotBlank @Size(min = 2, max = 100)
    private String fullName;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
