package com.devhoard.security; // Security utility layer for JWT operations

// JSON Web Token (JWT) library imports for secure token handling
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Java Cryptography and standard utility imports
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for JSON Web Token lifecycle management.
 * Handles the generation, parsing, and cryptographic verification of identity tokens.
 */
@Component
public class JwtUtils {

    // Injection of the Base64-encoded secret key from the application properties
    @Value("${devhoard.jwt.secret}")
    private String jwtSecretBase64; 

    // Injection of the defined expiration threshold (currently utilized if programmatic defaults fail)
    @Value("${devhoard.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Derives a cryptographic HMAC-SHA key from the Base64-encoded secret.
     * This key is used for both signing and verifying the integrity of JWTs.
     */
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretBase64));
    }

    /**
     * Generates a stateless authentication token for the specified principal.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // Assigning the username to the standard 'sub' claim
                .issuedAt(new Date()) // Recording the precise time of issuance
                // Expiration logic: Currently set to 24 hours (86,400,000ms) for development stability
                .expiration(new Date((new Date()).getTime() + 86400000)) 
                .signWith(getSigningKey()) // Signing the payload with the HMAC-SHA secret
                .compact(); // Final serialization into the compact Header.Payload.Signature string format
    }

    /**
     * Interrogates a token string to extract the authenticated subject.
     * Implements strict cryptographic verification: if the signature is invalid, parsing will fail.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Verification: Validating the signature against the local secret
                .build()
                .parseSignedClaims(token) // Decomposition: Deciphering the JWS payload
                .getPayload()
                .getSubject(); // Extraction: Retrieving the 'sub' claim (username)
    }
}

