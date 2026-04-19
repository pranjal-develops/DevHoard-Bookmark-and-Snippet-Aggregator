package com.devhoard.DTO;

public class AuthRequest {
    private String username;
    private String password;
    private String guestId;

    public AuthRequest() {
    }

    public AuthRequest(String username, String password, String guestId) {
        this.username = username;
        this.password = password;
        this.guestId = guestId;
    }

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
