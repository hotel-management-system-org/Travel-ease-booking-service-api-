package com.travel_ease.horel_system.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String extractUserId(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Authentication principal is not a JWT");
        }
        return jwt.getSubject();
    }

    public String extractUsername(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Authentication principal is not a JWT");
        }
        return jwt.getClaimAsString("preferred_username");
    }
}
