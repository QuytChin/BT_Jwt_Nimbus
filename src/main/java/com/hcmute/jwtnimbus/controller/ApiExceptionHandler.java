package com.hcmute.jwtnimbus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Bước Exception trong slide: 401 cho sai đăng nhập/JWT, 403 cho khóa tài khoản/không có quyền. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> badCredentials(BadCredentialsException ex) {
        return body(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Email hoặc mật khẩu không đúng");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> locked(LockedException ex) {
        return body(HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED", "Tài khoản đã bị khóa");
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<Map<String, Object>> accountStatus(AccountStatusException ex) {
        return body(HttpStatus.FORBIDDEN, "ACCOUNT_STATUS", "Trạng thái tài khoản không cho phép đăng nhập");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accessDenied(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Không được phép truy cập tài nguyên");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> responseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return body(status, "REQUEST_ERROR", ex.getReason() == null ? "Yêu cầu không hợp lệ" : ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("Dữ liệu không hợp lệ");
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("error", code);
        result.put("message", message);
        return ResponseEntity.status(status).body(result);
    }
}
