package com.devhoard.DTO; // Layer for Data Transfer Objects, isolating API contracts from the underlying entity structure

/**
 * Data Transfer Object representing a successful authentication response.
 * Delivers the session token and the authenticated principal's identification to the client.
 */
public class AuthResponse {

    /**
     * The cryptographically signed JWT used for subsequent request authorization.
     */
    private String token;
    
    /**
     * The confirmed username of the authenticated principal.
     */
    private String username;

    /**
     * Default no-args constructor required for framework-based reflection and JSON deserialization.
     */
    public AuthResponse() {
    }

    /**
     * Overloaded constructor for initialization upon successful credential verification.
     */
    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    /* 
     * Manual Accessors: 
     * Maintained for absolute control over the DTO contract without additional dependency overhead.
     */
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

