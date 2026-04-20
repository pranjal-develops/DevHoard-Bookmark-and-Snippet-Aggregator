package com.devhoard.repository; // Data access layer for bookmark persistence

// Internal domain entities and Spring Data JPA primitives
import com.devhoard.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for Bookmark entity persistence.
 * Extends JpaRepository to provide standard CRUD operations and custom JPQL queries.
 */
public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {

    // Standard derived query methods for simple lookups
    List<Bookmark> findByTitleContainingIgnoreCase(String keyword);
    List<Bookmark> findByCategoriesContaining(String category);
    List<Bookmark> findByIsFavoriteTrue();
    List<Bookmark> findByGuestId(String guestId);

    /**
     * Retrieves bookmarks for a specific owner, supporting both registered users and anonymous guests.
     * Implements a fallback logic: if a User is associated, it filters by username; 
     * otherwise, it filters by the guest identifier.
     */
    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "(u.username = :username) OR " +
            "(u IS NULL AND b.guestId = :guestId)")
    List<Bookmark> findByOwner(String username, String guestId);

    /**
     * Performs a case-insensitive keyword search restricted to the specified owner's archive.
     */
    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Bookmark> searchByOwner(String username, String guestId, String keyword);

    /**
     * Filters the owner's archive by a specific category tag.
     * Uses the JPQL 'MEMBER OF' operator to query within the @ElementCollection set.
     */
    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND :category MEMBER OF b.categories")
    List<Bookmark> findByCategoryAndOwner(String username, String guestId, String category);

    /**
     * Retrieves favorited bookmarks for the specified owner.
     */
    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND b.isFavorite = true")
    List<Bookmark> findFavoritesByOwner(String username, String guestId);
}

