package com.hcmute.jwtnimbus.service;

import com.hcmute.jwtnimbus.exception.JwtExpiredException;
import com.hcmute.jwtnimbus.exception.JwtInvalidException;
import com.hcmute.jwtnimbus.exception.JwtSignatureException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * JWT Service dùng hoàn toàn Nimbus JOSE + JWT.
 * Chức năng tương đương JwtService trong slide nhưng không dùng io.jsonwebtoken.
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return getVerifiedClaims(token).getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + jwtExpiration);

            List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(now)
                .expirationTime(expiration)
                .claim("authorities", authorities)
                .build();

            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(),
                claims
            );
            signedJWT.sign(new MACSigner(getSecretBytes()));
            return signedJWT.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException("Không thể tạo JWT bằng Nimbus", ex);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        JWTClaimsSet claims = getVerifiedClaims(token);
        String subject = claims.getSubject();
        Date expiration = claims.getExpirationTime();
        return subject != null
            && subject.equals(userDetails.getUsername())
            && expiration != null
            && expiration.after(new Date());
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private JWTClaimsSet getVerifiedClaims(String token) {
        final SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException ex) {
            throw new JwtInvalidException("JWT không đúng định dạng", ex);
        }

        if (!JWSAlgorithm.HS256.equals(signedJWT.getHeader().getAlgorithm())) {
            throw new JwtInvalidException("Thuật toán JWT không được hỗ trợ");
        }

        final boolean verified;
        try {
            verified = signedJWT.verify(new MACVerifier(getSecretBytes()));
        } catch (JOSEException ex) {
            throw new JwtInvalidException("Không thể kiểm tra JWT", ex);
        }

        if (!verified) {
            throw new JwtSignatureException("Chữ ký JWT không hợp lệ");
        }

        final JWTClaimsSet claims;
        try {
            claims = signedJWT.getJWTClaimsSet();
        } catch (ParseException ex) {
            throw new JwtInvalidException("Claims trong JWT không hợp lệ", ex);
        }

        Date expiration = claims.getExpirationTime();
        if (expiration == null || !expiration.after(new Date())) {
            throw new JwtExpiredException("JWT đã hết hạn");
        }

        return claims;
    }

    private byte[] getSecretBytes() {
        final byte[] key;
        try {
            key = Base64.getDecoder().decode(secretKey);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("security.jwt.secret-key phải là chuỗi Base64 hợp lệ", ex);
        }

        if (key.length < 32) {
            throw new IllegalStateException("JWT secret phải có ít nhất 256 bit cho HS256");
        }
        return key;
    }
}
