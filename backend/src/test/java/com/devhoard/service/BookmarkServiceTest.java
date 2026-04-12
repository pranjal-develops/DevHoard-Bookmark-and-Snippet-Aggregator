package com.devhoard.service;

import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.repository.BookmarkRepo;
import com.devhoard.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Launches a Mockito-controlled lab
public class BookmarkServiceTest {

    @Mock
    private BookmarkRepo bookmarkRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    void verifyOwnership_ShouldThrowException_WhenNotOwner() {
        // 1. Setup: A bookmark owned by 'userA'
        Bookmark bookmark = new Bookmark();
        bookmark.setId(1L);
        User owner = new User();
        owner.setUsername("userA");
        bookmark.setUser(owner);

        // Tell Mockito: "When someone asks for ID 1, return this UserA bookmark"
        when(bookmarkRepo.findById(1L)).thenReturn(Optional.of(bookmark));

        // 🧪 THE CHALLENGE: 'userB' attempts access
        // We verify that the guard throws a RuntimeException
        assertThrows(RuntimeException.class, () -> {
            bookmarkService.deleteBookmark(1L, "userB", "guest_999");
        });
    }

    @Test
    void verifyOwnership_ShouldPass_WhenGuestMatches() {
        // 1. Setup: A bookmark currently owned by a guest session
        Bookmark bookmark = new Bookmark();
        bookmark.setId(2L);
        bookmark.setGuestId("session_123");

        when(bookmarkRepo.findById(2L)).thenReturn(Optional.of(bookmark));

        // 🧪 THE CHALLENGE: Access with the matching session ID
        // This should execute successfully (no exception)
        bookmarkService.deleteBookmark(2L, null, "session_123");
    }

    @Test
    void deleteBookmark_ShouldThrowException_WhenNotOwner() {
        // 1. Setup: A bookmark owned by 'dragon'
        Bookmark bookmark = new Bookmark();
        bookmark.setId(101L);
        User owner = new User();
        owner.setUsername("dragon");
        bookmark.setUser(owner);

        when(bookmarkRepo.findById(101L)).thenReturn(Optional.of(bookmark));

        // 🧪 THE CHALLENGE: 'intruder' attempts access
        // We verify the guard throws an exception
        assertThrows(RuntimeException.class, () -> {
            bookmarkService.deleteBookmark(101L, "intruder", "session_999");
        });
    }

    @Test
    void toggleFavorite_ShouldThrowException_WhenNotOwner() {
        // 1. Setup: A bookmark owned by 'dragon'
        Bookmark bookmark = new Bookmark();
        bookmark.setId(101L);
        User owner = new User();
        owner.setUsername("dragon");
        bookmark.setUser(owner);

        when(bookmarkRepo.findById(101L)).thenReturn(Optional.of(bookmark));

        // 🧪 THE CHALLENGE: 'intruder' attempts access
        // We verify the guard throws an exception
        assertThrows(RuntimeException.class, () -> {
            bookmarkService.toggleFavorite(101L, "intruder", "session_999");
        });
    }

}
