package com.project.projectService.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String token;

    public String getUsernameFromToken(String token) {
        return getFromToken(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        return getFromToken(token).get("roles", List.class);
    }

    public Claims getFromToken(String token) {
        return Jwts.parser()
                .parseClaimsJws(token)
                .getBody();
    }
}
