package com.devhoard.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${devhoard.jwt.secret}")
    private String jwtSecretBase64; // base64-encoded secret (recommended)

    @Value("${devhoard.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretBase64));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + 86400000))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Use your seal to check the wax
                .build()
                .parseSignedClaims(token) // Read the paper inside
                .getPayload()
                .getSubject(); // Get the username!
    }


}
