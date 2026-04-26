package com.devhoard.controller;

import com.devhoard.DTO.BookmarkRequest;
import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBookmark(@Valid @RequestBody BookmarkRequest payload) {
        String username = extractUsername();
        bookmarkService.scrapeAndSave(payload.getUrl(), payload.getCategories(), payload.getGuestId(), username);
    }

    @GetMapping
    public List<Bookmark> getBookmarks(@RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean favoritesOnly,
            @RequestParam String guestId) {
        String username = extractUsername();

        if (favoritesOnly)
            return bookmarkService.getFavorites(username, guestId);
        if (category != null && !category.isBlank())
            return bookmarkService.getByCategory(category, username, guestId);
        if (q == null || q.isBlank())
            return bookmarkService.getAll(username, guestId);

        return bookmarkService.search(q, username, guestId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookmark(@PathVariable Long id, @RequestParam String guestId) {
        String username = extractUsername();
        bookmarkService.deleteBookmark(id, username, guestId);
    }

    @PatchMapping("/{id}/category")
    public Bookmark updateCategory(@PathVariable Long id, @Valid @RequestBody BookmarkRequest payload) {
        String username = extractUsername();
        return bookmarkService.updateCategory(id, payload.getCategories(), username, payload.getGuestId());
    }

    @PatchMapping("/{id}/favorite")
    public Bookmark toggleFavorite(@PathVariable Long id, @Valid @RequestBody BookmarkRequest payload) {
        String username = extractUsername();
        return bookmarkService.toggleFavorite(id, username, payload.getGuestId());
    }

    private String extractUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getUsername();
            }
            return auth.getName();
        }
        return null;
    }

    @PostMapping("/migrate-guest")
    public void migrateGuest(@RequestParam String from, @RequestParam String to) {
        bookmarkService.migrateGuestBookmarks(from, to);
    }

}
