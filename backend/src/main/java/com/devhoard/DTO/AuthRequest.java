package com.devhoard.DTO; // Layer for Data Transfer Objects, isolating the API schema from domain entities

/**
 * Data Transfer Object representing an authentication or registration request.
 * Encapsulates the credentials and session information required for identity management.
 */
public class AuthRequest {

    /**
     * The unique identity handle for the user.
     */
    private String username;
    
    /**
     * The raw password string provided during registration or authentication.
     * Note: This is encoded immediately upon reaching the service tier.
     */
    private String password;
    
    /**
     * Optional identifier for an existing anonymous guest session.
     * Utilized during registration/login to migrate 'orphaned' bookmarks to the new identity.
     */
    private String guestId;

    /**
     * Default no-args constructor required for framework-based reflection and JSON deserialization.
     */
    public AuthRequest() {
    }

    /**
     * Overloaded constructor for manual instantiation.
     */
    public AuthRequest(String username, String password, String guestId) {
        this.username = username;
        this.password = password;
        this.guestId = guestId;
    }

    /* 
     * Manual Accessors: 
     * Provided for structured access to private fields throughout the service layer.
     */
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }
}

