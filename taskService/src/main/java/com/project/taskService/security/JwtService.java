package com.project.taskService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtService {

    @Value("${spring.token.signing.key}")
    private String jwtSigningKey;

    public String getUsernameFromToken(String token) {
        return getFromToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getFromToken(token).get("roles", List.class);
    }

    public Claims getFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSigningKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
