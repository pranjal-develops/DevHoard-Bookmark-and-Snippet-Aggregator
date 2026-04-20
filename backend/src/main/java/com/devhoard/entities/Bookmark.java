package com.devhoard.entities; // Domain model layer for persistence entities

// Jakarta Persistence API (JPA) and Hibernate annotations for ORM mapping
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Entity representing a persisted bookmark.
 * Utilizes JPA for mapping to the relational database and Jackson for JSON
 * serialization control.
 */
@Entity
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key using identity-based auto-generation

    private String originalUrl; // The source URL of the bookmarked resource

    private String title; // Extracted or user-defined title for the bookmark

    // Description field with a hard character limit to prevent database overflow
    // from long meta-descriptions
    @Column(name = "description", nullable = true, length = 512)
    private String description;

    private String imgUrl; // Path or URL to the extracted thumbnail asset

    /**
     * Set of categories/tags associated with the bookmark.
     * 
     * @ElementCollection signals JPA to create a separate joined table
     *                    (CollectionTable)
     *                    to manage this many-to-one relationship of basic types
     *                    (Strings).
     */
    @ElementCollection
    @CollectionTable // This tells JPA to create a separate table behind the scenes to keep track of
                     // all
                     // the tags without us needing to do it manually!
    private Set<String> categories;

    /**
     * Flag indicating if the bookmark is marked as a 'favorite'.
     * 
     * @JsonProperty ensures the boolean getter follows the expected JSON naming
     *               convention.
     */
    @JsonProperty("isFavorite")
    private boolean isFavorite = false;

    /**
     * Automatic timestamping handled by Hibernate during the initial persist
     * operation.
     */
    @CreationTimestamp
    private ZonedDateTime createdAt;

    /**
     * Unique identifier for anonymous users.
     * Facilitates bookmark management before a user officially registers.
     */
    private String guestId;

    /**
     * Relationship mapping to the registered owner.
     * FetchType.LAZY prevents unnecessary user data loading when only bookmark
     * metadata is required.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Default constructor required by JPA for entity instantiation.
     */
    public Bookmark() {
    }

    /**
     * Overloaded constructor for initialization during the fast-scrape phase.
     */
    public Bookmark(String originalUrl, String title, String description, String imgUrl) {
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = null;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    /**
     * Overloaded constructor including initial category assignment.
     */
    public Bookmark(String originalUrl, String title, String description, String imgUrl, Set<String> categories) {
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = categories;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    /**
     * comprehensive constructor for legacy guest assignment logic.
     */
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

    /*
     * Manual Getters and Setters:
     * While Lombok's @Data is an alternative, manual definitions are maintained
     * here
     * to ensure absolute clarity on the entity's contract.
     */

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
