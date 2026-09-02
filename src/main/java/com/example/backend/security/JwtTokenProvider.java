package com.example.backend.security;

import com.example.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration-ms}") long expirationMs) {

                                this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
                                this.expirationMs = expirationMs;

    }


public String generateToken(User user) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
    .subject(user.getEmail())
    .claim("userId", user.getId())
    .claim("role", user.getRole().name())
    .issuedAt(now)
    .expiration(expiry)
    .signWith(key)
    .compact();  //compact() serializes and signs it into the final string
}

public boolean isValid(String token) {
    try{ 
        parseClaims(token);
        return true;
    } catch(JwtException | IllegalArgumentException ex) {
        return false;
    }
 
}

public String extractEmail(String token) {
    return parseClaims(token).getSubject();  //email
}



private Claims parseClaims(String token) {
    return Jwts.parser()
    .verifyWith(key) //recomputes the HMAC with your secret and compares. Mismatch → SignatureException. This is what makes a forged or edited token useless
    .build()
    .parseSignedClaims(token) //decodes and validates. Also checks exp automatically → ExpiredJwtException if past expiry. SignedClaims (not just Claims) means it refuses an unsigned token — that closes the alg: none attack.
    .getPayload(); //hands back the Claims map you put in: subject, userId, role
}



    
}
