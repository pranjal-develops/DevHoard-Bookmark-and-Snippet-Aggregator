package com.devhoard.repository;

import com.devhoard.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByTitleContainingIgnoreCase(String keyword);
    List<Bookmark> findByCategoriesContaining(String category);
    List<Bookmark> findByIsFavoriteTrue();
    List<Bookmark> findByGuestId(String guestId);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "(u.username = :username) OR " +
            "(u IS NULL AND b.guestId = :guestId)")
    List<Bookmark> findByOwner(String username, String guestId);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Bookmark> searchByOwner(String username, String guestId, String keyword);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND :category MEMBER OF b.categories")
    List<Bookmark> findByCategoryAndOwner(String username, String guestId, String category);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Bookmark b LEFT JOIN b.user u WHERE " +
            "((u.username = :username) OR (u IS NULL AND b.guestId = :guestId)) " +
            "AND b.isFavorite = true")
    List<Bookmark> findFavoritesByOwner(String username, String guestId);



}
