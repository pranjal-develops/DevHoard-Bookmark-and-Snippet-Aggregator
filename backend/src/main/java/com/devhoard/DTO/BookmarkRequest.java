package com.devhoard.DTO;

import java.util.Set;

@jakarta.validation.constraints.NotNull
public class BookmarkRequest {

    private String url;
    private Set<String> categories;
    private String guestId;

    // No-Args Constructor (Required for Jackson)
    public BookmarkRequest() {
    }

    public BookmarkRequest(String url, Set<String> categories, String guestId) {
        this.url = url;
        this.categories = categories;
        this.guestId = guestId;
    }

    // Manual Getters & Setters
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
