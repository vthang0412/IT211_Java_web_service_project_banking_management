package com.rikkei.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Getter
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes()
        );
    }

    public String generateAccessToken(
            UserPrincipal principal
    ) {

        Date now = new Date();

        Date expiry =
                new Date(
                        now.getTime() + accessExpiration
                );

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("role",
                        principal.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(
            UserPrincipal principal
    ) {

        Date now = new Date();

        Date expiry =
                new Date(
                        now.getTime() + refreshExpiration
                );

        return Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(
            String token
    ) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(
            String token
    ) {

        try {

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception ex) {

            return false;
        }
    }
}