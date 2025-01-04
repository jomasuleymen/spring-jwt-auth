package com.demo.auth.auth.jwt;

import com.demo.auth.core.configurations.security.UserDetailsImpl;
import io.jsonwebtoken.JwtException;

public interface JwtService {
    String generateToken(UserDetailsImpl payload, int ttlInSec);

    UserDetailsImpl getPayload(String token) throws JwtException;
}
