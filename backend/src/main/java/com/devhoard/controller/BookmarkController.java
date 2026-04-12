package com.devhoard.controller;

import com.devhoard.DTO.BookmarkRequest;
import com.devhoard.entities.Bookmark;
import com.devhoard.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = null;
                   if (auth!=null
                           && auth.isAuthenticated()
                           && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                                Object principal = auth.getPrincipal();
                                if (principal instanceof com.devhoard.entities.User) {
                                    username = ((com.devhoard.entities.User) principal).getUsername();
                                } else {
                                    username = auth.getName();
                                }
                   }
            System.out.println("🛰️ [Identity Handover] User: " + username + " | GuestId: " + payload.getGuestId());
            bookmarkService.scrapeAndSave(payload.getUrl(), payload.getCategories(), payload.getGuestId(), username);
        }   catch (Exception e)  {
            throw new RuntimeException("An error has occurred:", e);
        }
    }


    @GetMapping
    public List<Bookmark> getBookmarks(@RequestParam(required = false) String q, @RequestParam(required = false)String category, @RequestParam(defaultValue = "false") boolean favoritesOnly, @RequestParam String guestId){

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = null;
//        if (auth != null && auth.isAuthenticated() &&
//                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
//            Object principal = auth.getPrincipal();
//            if (principal instanceof com.devhoard.entities.User) {
//                username = ((com.devhoard.entities.User) principal).getUsername();
//            } else {
//                username = auth.getName();
//            }
//        }

        String username = extractUsername();
        if (favoritesOnly) return bookmarkService.getFavorites(username, guestId);
        if(category!=null && !category.isBlank()) return bookmarkService.getByCategory(category, username, guestId);
        if(q == null  || q.isBlank()) return bookmarkService.getAll(username, guestId);
        return bookmarkService.search(q, username, guestId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookmark(@PathVariable Long id, @RequestParam String guestId){
        String username = extractUsername();
        bookmarkService.deleteBookmark(id, username,guestId);
    }

    @PatchMapping("/{id}/category")
    public Bookmark updateCategory(@PathVariable Long id,@Valid @RequestBody BookmarkRequest payload) {
        Set<String> newCategories = payload.getCategories();
        String username = extractUsername();
        return bookmarkService.updateCategory(id, newCategories, username,payload.getGuestId());
    }

    @PatchMapping("/{id}/favorite")
    public Bookmark toggleFavorite(@PathVariable Long id, @Valid @RequestBody BookmarkRequest payload) {
        String username = extractUsername();
        return bookmarkService.toggleFavorite(id,username, payload.getGuestId());
    }

    private String extractUsername(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.devhoard.entities.User) {
                return ((com.devhoard.entities.User) principal).getUsername();
            } else {
                return auth.getName();
            }
        }
        return null;
    }

}
