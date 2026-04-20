package com.devhoard.DTO; // Layer for Data Transfer Objects, decoupling the API from the Domain models

// Standard Java collection imports
import java.util.Set;

/**
 * Data Transfer Object representing a client-side request to persist or modify a bookmark.
 * Used for receiving JSON payloads and mapping them to the application's internal logic.
 */
@jakarta.validation.constraints.NotNull
public class BookmarkRequest {

    /**
     * The primary resource URL provided by the client.
     */
    private String url;
    
    /**
     * A collection of tag strings associated with the requested bookmark.
     */
    private Set<String> categories;
    
    /**
     * Unique identifier for guest sessions. 
     * Essential for correlating bookmarks with anonymous users before official account registration.
     */
    private String guestId;

    /**
     * Default no-args constructor required by the Jackson library for JSON-to-object deserialization.
     */
    public BookmarkRequest() {
    }

    /**
     * All-args constructor for manual instantiation and testing.
     */
    public BookmarkRequest(String url, Set<String> categories, String guestId) {
        this.url = url;
        this.categories = categories;
        this.guestId = guestId;
    }

    /* 
     * Manual Accessors: 
     * Maintained here to ensure a rigid contract for the DTO without additional dependency overhead.
     */
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }
}

