package com.devhoard.repository;

import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

@DataJpaTest
public class BookmarkRepoTest {

    @Autowired
    private BookmarkRepo bookmarkRepo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindByOwner_IsolatesUsers(){
        User userA = new User();
        userA.setUsername("userA");
        userA.setPassword("pass");
        entityManager.persist(userA);

        User userB = new User();
        userB.setUsername("userB");
        userB.setPassword("pass");
        entityManager.persist(userB);

        Bookmark bA = new Bookmark("http://a.com", "Title A", "Desc", null, Set.of("tech"));
        bA.setUser(userA);
        entityManager.persist(bA);

        Bookmark bB = new Bookmark("http://b.com", "Title B", "Desc", null, Set.of("news"));
        bB.setUser(userB);
        entityManager.persist(bB);
        entityManager.flush();

        List<Bookmark> result = bookmarkRepo.findByOwner("userA", "some_guest_id");
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Title A");
        assertThat(result.getFirst().getUser().getUsername()).isEqualTo("userA");

    }

    @Test
    void testFindByOwner_HybridAccess() {
        // 1. Setup: One User bookmark, one Guest bookmark
        User user = new User();
        user.setUsername("dragon");
        user.setPassword("pass");
        entityManager.persist(user);

        // Bookmark 1: Linked to account
        Bookmark b1 = new Bookmark("http://user.com", "User Link", "Desc", null, Set.of("tech"));
        b1.setUser(user);
        entityManager.persist(b1);

        // Bookmark 2: Linked to a session ID
        Bookmark b2 = new Bookmark("http://guest.com", "Guest Link", "Desc", null, Set.of("news"));
        b2.setGuestId("session_123");
        entityManager.persist(b2);

        entityManager.flush();

        // 🧪 THE CHALLENGE: Searching with both identities
        List<Bookmark> results = bookmarkRepo.findByOwner("dragon", "session_123");

        // 🛡️ THE VERDICT: Both must be visible to the user!
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Bookmark::getTitle)
                .containsExactlyInAnyOrder("User Link", "Guest Link");
    }

}
