package com.thatbackendguy.quizapp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils
{

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init()
    {

        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    @Value("${jwt.timeout}")
    private long expiration;

    public String generateToken(String username)
    {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token)
    {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, String username)
    {

        final String extractedUsername = extractClaims(token).getSubject();

        return ( extractedUsername.equals(username) && !isTokenExpired(token) );
    }

    private boolean isTokenExpired(String token)
    {

        return extractClaims(token).getExpiration().before(new Date());
    }

}
