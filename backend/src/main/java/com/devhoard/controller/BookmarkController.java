package com.devhoard.controller;

import com.devhoard.entities.Bookmark;
import com.devhoard.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    public Bookmark saveBookmark(@RequestBody Map<String, String> payload) throws IOException {
//        String url = payload.get("url");
//        return bookmarkService.scrapeAndSave(url);
//    }
    public Bookmark saveBookmark(@RequestBody Map<String ,String> payload){
        try {
            String url = payload.get("url");
            String category = payload.getOrDefault("category", "Uncategorized");
            if(category == null || category.isBlank()) category = "Uncategorized";
            return bookmarkService.scrapeAndSave(url, category);
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
    public Bookmark updateCategory(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newCategory = payload.get("category");
        return bookmarkService.updateCategory(id, newCategory);
    }

    @PatchMapping("/{id}/favorite")
    public Bookmark toggleFavorite(@PathVariable Long id) {
        return bookmarkService.toggleFavorite(id);
    }

}
