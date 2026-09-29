package com.hcmute.jwtnimbus.dto;

public class LoginResponse {
    private final String token;
    private final long expiresIn;
    private final String tokenType = "Bearer";
    private final UserResponse user;
    public LoginResponse(String token, long expiresIn, UserResponse user) {
        this.token = token; this.expiresIn = expiresIn; this.user = user;
    }
    public String getToken() { return token; }
    public long getExpiresIn() { return expiresIn; }
    public String getTokenType() { return tokenType; }
    public UserResponse getUser() { return user; }
}
