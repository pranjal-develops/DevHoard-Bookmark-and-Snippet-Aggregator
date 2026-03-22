package com.devhoard.repository;

import com.devhoard.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByTitleContainingIgnoreCase(String keyword);

}
