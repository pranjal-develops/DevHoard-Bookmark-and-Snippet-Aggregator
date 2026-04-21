package com.devhoard.repository;

import com.devhoard.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByGuestId(String guestId);

    // Dynamic owner filtering supporting both registered users and guests
    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "(u.username = :username) OR " +
            "(u IS NULL AND b.guestId = :guestId)")
    List<Bookmark> findByOwner(String username, String guestId);

    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Bookmark> searchByOwner(String username, String guestId, String keyword);

    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND :category MEMBER OF b.categories")
    List<Bookmark> findByCategoryAndOwner(String username, String guestId, String category);

    @Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND b.isFavorite = true")
    List<Bookmark> findFavoritesByOwner(String username, String guestId);
}


