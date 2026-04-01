package com.devhoard.controller;

import com.devhoard.DTO.BookmarkRequest;
import com.devhoard.entities.Bookmark;
import com.devhoard.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBookmark(@Valid @RequestBody BookmarkRequest payload){
        try {
            String url = payload.getUrl();
            Set<String> categories = payload.getCategories();
//            if(categories == null || categories.isEmpty()) categories = new HashSet<>(Collections.singletonList("Uncategorized"));
            bookmarkService.scrapeAndSave(url, categories);
        }   catch (Exception e)  {
            throw new RuntimeException("An error has occurred:", e);
        }
    }


    @GetMapping
    public List<Bookmark> getBookmarks(@RequestParam(required = false) String q, @RequestParam(required = false)String category, @RequestParam(defaultValue = "false") boolean favoritesOnly){
        if (favoritesOnly) return bookmarkService.getFavorites();
        if(category!=null && !category.isBlank()) return bookmarkService.getByCategory(category);
        if(q == null  || q.isBlank()) return bookmarkService.getAll();
        return bookmarkService.search(q);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookmark(@PathVariable Long id){
        bookmarkService.deleteBookmark(id);
    }

    @PatchMapping("/{id}/category")
    public Bookmark updateCategory(@PathVariable Long id,@Valid @RequestBody BookmarkRequest payload) {
        Set<String> newCategories = payload.getCategories();
        return bookmarkService.updateCategory(id, newCategories);
    }

    @PatchMapping("/{id}/favorite")
    public Bookmark toggleFavorite(@PathVariable Long id) {
        return bookmarkService.toggleFavorite(id);
    }

}
