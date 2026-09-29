package com.hcmute.jwtnimbus.filter;

import com.hcmute.jwtnimbus.exception.JwtExpiredException;
import com.hcmute.jwtnimbus.exception.JwtInvalidException;
import com.hcmute.jwtnimbus.exception.JwtSignatureException;
import com.hcmute.jwtnimbus.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Bước Filter trong slide: đọc Authorization: Bearer <token> và xác thực JWT. */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtExpiredException ex) {
            writeUnauthorized(response, "JWT_EXPIRED", "JWT đã hết hạn");
        } catch (JwtSignatureException ex) {
            writeUnauthorized(response, "JWT_SIGNATURE_INVALID", "Chữ ký JWT không hợp lệ");
        } catch (JwtInvalidException ex) {
            writeUnauthorized(response, "JWT_INVALID", ex.getMessage());
        } catch (Exception ex) {
            writeUnauthorized(response, "JWT_INVALID", "JWT không hợp lệ");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":401,\"error\":\"" + json(code)
            + "\",\"message\":\"" + json(message) + "\"}");
    }

    private String json(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n");
    }
}
