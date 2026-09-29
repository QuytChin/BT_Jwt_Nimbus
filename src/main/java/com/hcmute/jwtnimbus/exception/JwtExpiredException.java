package com.hcmute.jwtnimbus.exception;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) { super(message); }
    public JwtExpiredException(String message, Throwable cause) { super(message, cause); }
}
