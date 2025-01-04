package com.demo.auth.auth.jwt;

import com.demo.auth.core.configurations.JwtConfig;
import com.demo.auth.core.configurations.security.UserDetailsImpl;
import com.demo.auth.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtConfig jwtConfig;

    @Override
    public String generateToken(UserDetailsImpl userDetails, int ttlInSec) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userDetails.getId());
        claims.put("username", userDetails.getUsername());
        claims.put("role", userDetails.getRole().name());

        return generateToken(claims, ttlInSec);
    }

    @Override
    public UserDetailsImpl getPayload(String token) throws JwtException {
        Claims claims = extractAllClaims(token);

        return UserDetailsImpl.builder()
                .id(claims.get("id", Long.class))
                .username(claims.get("username", String.class))
                .role(UserRole.valueOf(claims.get("role", String.class)))
                .build();
    }

    private String generateToken(Map<String, Object> extraClaims, int expirationInMs) {
        Date now = new Date();

        return Jwts.builder()
                .claims(extraClaims)
                .issuedAt(now)
                .expiration(DateUtils.addSeconds(now, expirationInMs))
                .signWith(getSecretKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
