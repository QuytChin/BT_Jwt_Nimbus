package com.hcmute.jwtnimbus.exception;

public class JwtInvalidException extends RuntimeException {
    public JwtInvalidException(String message) { super(message); }
    public JwtInvalidException(String message, Throwable cause) { super(message, cause); }
}
