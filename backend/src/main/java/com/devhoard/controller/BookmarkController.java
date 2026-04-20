package com.devhoard.controller; // API layer for bookmark orchestration

// DTOs and domain entities for structured request/response handling
import com.devhoard.DTO.BookmarkRequest;
import com.devhoard.entities.Bookmark;
import com.devhoard.service.BookmarkService;

// Jakarta validation and Spring Security primitives
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.io.IOException;
import java.util.*;

/**
 * REST Controller for managing bookmark operations.
 * Handles authentication context resolution and delegates business logic to the service tier.
 */
@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"}) // CORS configuration for local development environments
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * Dependency injection via constructor to ensure instance state remains immutable.
     */
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /**
     * POST endpoint for bookmark creation and asynchronous scraping.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBookmark(@Valid @RequestBody BookmarkRequest payload) {
        // Log entry point for request tracking
        System.out.println("[GATEKEEPER] POST Request Received for URL: " + payload.getUrl());
        
        try {
            // Extraction of authentication context from the current execution thread
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = null;
            
            // Validation of principal identity: Distinguishing between authenticated users and guest sessions
            if (auth != null
                    && auth.isAuthenticated()
                    && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                
                Object principal = auth.getPrincipal();
                
                // Polymorphic principal handling: Depending on context, principal may be a User object or a String username
                if (principal instanceof com.devhoard.entities.User) {
                    username = ((com.devhoard.entities.User) principal).getUsername();
                } else {
                    username = auth.getName();
                }
            }
            
            // Logging hand-over status for debugging identity propagation
            System.out.println("[Identity Handover] User: " + username + " | GuestId: " + payload.getGuestId());
            
            // Delegate long-running scraping task to the service layer; @Async handles the backgrounding
            bookmarkService.scrapeAndSave(payload.getUrl(), payload.getCategories(), payload.getGuestId(), username);
            
        } catch (Exception e) {
            // Generic exception propagation; ideally should be replaced by a GlobalExceptionHandler
            throw new RuntimeException("Internal failure during bookmark processing:", e);
        }
    }

    /**
     * GET endpoint supporting faceted search, categorization, and star-filtering.
     */
    @GetMapping
    public List<Bookmark> getBookmarks(@RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean favoritesOnly, 
            @RequestParam String guestId) {

        // Resolution of the current principal to filter repository results by owner
        String username = extractUsername();
        
        // Logical routing based on query parameter specificity
        if (favoritesOnly)
            return bookmarkService.getFavorites(username, guestId);
        
        if (category != null && !category.isBlank())
            return bookmarkService.getByCategory(category, username, guestId);
        
        if (q == null || q.isBlank())
            return bookmarkService.getAll(username, guestId);
        
        // Full-text search fallback
        return bookmarkService.search(q, username, guestId);
    }

    /**
     * DELETE endpoint with ownership verification logic encapsulated in the service tier.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookmark(@PathVariable Long id, @RequestParam String guestId) {
        String username = extractUsername();
        bookmarkService.deleteBookmark(id, username, guestId);
    }

    /**
     * PATCH endpoint for granular category/tag updates.
     */
    @PatchMapping("/{id}/category")
    public Bookmark updateCategory(@PathVariable Long id, @Valid @RequestBody BookmarkRequest payload) {
        Set<String> newCategories = payload.getCategories();
        String username = extractUsername();
        return bookmarkService.updateCategory(id, newCategories, username, payload.getGuestId());
    }

    /**
     * PATCH endpoint for toggling favorited/starred status.
     */
    @PatchMapping("/{id}/favorite")
    public Bookmark toggleFavorite(@PathVariable Long id, @Valid @RequestBody BookmarkRequest payload) {
        String username = extractUsername();
        return bookmarkService.toggleFavorite(id, username, payload.getGuestId());
    }

    /**
     * Internal utility to isolate Spring Security principal extraction logic.
     * Returns null if the current session is an anonymous guest.
     */
    private String extractUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Verification that the token is valid, authenticated, and not an anonymous placeholder
        if (auth != null && auth.isAuthenticated() &&
                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            
            Object principal = auth.getPrincipal();
            
            // Consistency check: Resolve underlying username from potentially nested user entities
            if (principal instanceof com.devhoard.entities.User) {
                return ((com.devhoard.entities.User) principal).getUsername();
            } else {
                return auth.getName();
            }
        }
        return null; // Signals a guest identity session
    }
}

