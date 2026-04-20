package com.devhoard.entities; // Domain model layer for identity entities

// Jackson and Jakarta Persistence annotations for serialization and ORM mapping
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Entity representing a registered user account.
 * Handles the relational mapping for user identification and their associated
 * collection of bookmarks.
 */
@Entity
@Table(name = "users") // Explicitly defining table name to avoid 'user' keyword conflicts in certain
                       // SQL dialects
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key for user unique identification

    @Column(unique = true) // Enforcing database-level uniqueness for user handles
    private String username;

    /**
     * Salt-encoded password hash.
     * 
     * @JsonIgnore ensures the credential hash is never leaked into the API response
     *             payloads.
     */
    @JsonIgnore
    @NotNull
    private String password;

    /**
     * Bi-directional relationship with the Bookmark entity.
     * mappedBy: Indicates that the 'Bookmark' entity owns the relationship mapping
     * (via 'user' field).
     * cascade: Ensures that user deletion propagates through to their archived
     * bookmarks.
     * 
     * @JsonIgnore: Critical to prevent infinite recursive recursion during JSON
     *              serialization.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore // This breaks the circular JSON loop of user and bookmarks being provided by
                // the
                // backend!
    private List<Bookmark> bookmarks;

    /**
     * Default constructor required for JPA proxy instantiation and reflection-based
     * frameworks.
     */
    public User() {
    }

    /**
     * Full-parameter constructor for manual instantiation in testing or migration
     * contexts.
     */
    public User(Long id, String username, String password, List<Bookmark> bookmarks) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.bookmarks = bookmarks;
    }

    /*
     * Manual Accessors:
     * Maintained for absolute control over the entity contract without external
     * boilerplate dependencies.
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }
}
