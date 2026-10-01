package com.terraguard.quakexit.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expirationMs;
    public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }
    public String generateToken(UserDetails user, Long userId, String role) {
        Date now = new Date();
        return Jwts.builder().subject(user.getUsername()).claim("userId", userId).claim("role", role)
            .issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(key, Jwts.SIG.HS256).compact();
    }
    public String extractUsername(String token) { return extractClaim(token, Claims::getSubject); }
    public <T> T extractClaim(String token, Function<Claims, T> resolver) { return resolver.apply(parse(token).getPayload()); }
    public boolean isTokenValid(String token, UserDetails user) { try { return extractUsername(token).equalsIgnoreCase(user.getUsername()) && !isTokenExpired(token); } catch (JwtException | IllegalArgumentException ex) { return false; } }
    public boolean isTokenExpired(String token) { return extractClaim(token, Claims::getExpiration).before(new Date()); }
    private Jws<Claims> parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token); }
}
