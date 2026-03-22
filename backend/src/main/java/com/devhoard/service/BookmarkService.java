package com.devhoard.service;

import com.devhoard.entities.Bookmark;
import com.devhoard.repository.BookmarkRepo;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepo bookmarkRepo;

    public Bookmark scrapeAndSave(String url) {
        try {
            //Some Sites like StackOverflow will block this as when JSoup makes an HTTP request to StackOverflow, it sends a secret header identifying itself as "Java/17.0.x".
            //StackOverflow's firewall immediately sees that you are a bot, not a human, and blocks your request with a 403 Forbidden status. Because JSoup crashes on a 403, your try/catch block catches the crash, throws your custom RuntimeException, and the entity is never saved.
//            Document document = Jsoup.connect(url).get();

            //This will "spoof" your identity so StackOverflow or any other such site thinks you are a real person using the Google Chrome browser!
//            Document document = Jsoup.connect(url)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
//                    .referrer("http://www.google.com")
//                    .get();

            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .referrer("https://www.google.com")
                    .get();

            String title = document.title();
            String description = document.select("meta[name=description]").attr("content");
            String imgUrl = document.select("meta[property=og:image]").attr("content");
            Bookmark bookmark = new Bookmark(url, title, description, imgUrl);
            return bookmarkRepo.save(bookmark);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the entity",e);
        }
    }

    public void deleteBookmark(Long id){
        bookmarkRepo.deleteById(id);
    }

    public List<Bookmark> search(String keyword){
        return bookmarkRepo.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Bookmark> getAll() {
        return bookmarkRepo.findAll();
    }

}
