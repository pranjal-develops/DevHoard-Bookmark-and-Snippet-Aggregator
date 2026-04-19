package com.devhoard.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String title;
    @Column(name = "description", nullable = true, length = 512)
    private String description;
    private String imgUrl;

    @ElementCollection
    @CollectionTable // This tells JPA to create a separate table behind the scenes to keep track of all
                     // the tags without us needing to do it manually!
    private Set<String> categories;

    @JsonProperty("isFavorite")
    private boolean isFavorite = false;

    @CreationTimestamp
    private ZonedDateTime createdAt;
    private String guestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    public Bookmark() {
    }

    public Bookmark(String originalUrl, String title, String description, String imgUrl) {
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = null;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    public Bookmark(String originalUrl, String title, String description, String imgUrl, Set<String> categories) {
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = categories;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    public Bookmark(String originalUrl, String title, String description, String imgUrl, String guestId,
            Set<String> categories) {
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.guestId = guestId;
        this.categories = categories;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    // Manual Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
