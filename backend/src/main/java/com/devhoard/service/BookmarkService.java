package com.devhoard.service;

import com.devhoard.entities.Bookmark;
import com.devhoard.repository.BookmarkRepo;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.jsoup.HttpStatusException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.PageLoadStrategy;
import java.time.Duration;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepo bookmarkRepo;

    @Async
    public void scrapeAndSave(String url, String category) {
        Document document = null;
        try {
            //Some Sites like StackOverflow will block this as when JSoup makes an HTTP request to StackOverflow, it sends a secret header identifying itself as "Java/17.0.x".
            //StackOverflow's firewall immediately sees that you are a bot, not a human, and blocks your request with a 403 Forbidden status. Because JSoup crashes on a 403, your try/catch block catches the crash, throws your custom RuntimeException, and the entity is never saved.
//            Document document = Jsoup.connect(url).get();

            //This will "spoof" your identity so StackOverflow or any other such site thinks you are a real person using the Google Chrome browser!
//            Document document = Jsoup.connect(url)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
//                    .referrer("http://www.google.com")
//                    .get();
            try{
                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .referrer("https://www.google.com")
                        .get();
            } catch (IOException e) {
                    document = scrapeWithSelenium(url);
            }
            if (document == null) throw new Exception("Failed to Scrape");
            String title = document.title();
            String description = document.select("meta[name=description]").attr("content");
            String imgUrl = firstNonEmpty(document, "meta[property=og:image]","meta[name=twitter:image]", "meta[itemprop=image]", "link[rel=image_src]","link[rel=apple-touch-icon]");
            Bookmark bookmark = new Bookmark(url, title, description, imgUrl, category);
            bookmarkRepo.save(bookmark);
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

    public List<Bookmark> getByCategory (String category){ return bookmarkRepo.findByCategory(category);}

    public Bookmark updateCategory(Long id, String category) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        bookmark.setCategory(category);
        return bookmarkRepo.save(bookmark);
    }

    public Bookmark toggleFavorite(Long id) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        bookmark.setFavorite(!bookmark.isFavorite());
        return bookmarkRepo.save(bookmark);
    }

    // Add a helper for the controller too
    public List<Bookmark> getFavorites() {
        return bookmarkRepo.findByIsFavoriteTrue();
    }

    private static String firstNonEmpty(Document doc, String... cssQueries) {
        for (String q : cssQueries) {
            String v = doc.select(q).attr(q.contains("meta") ? "abs:content" : "abs:href");
            if (v != null && !v.isEmpty()) return v;
        }
        return "";
    }

    private Document scrapeWithSelenium(String url) {
        // 1. Setup the invisible Chrome driver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Runs without a window & Use the 'new' headless engine
        options.addArguments("--disable-blink-features=AutomationControlled"); // Hides the "I am a robot" flag
        options.addArguments("--window-size=1920,1080"); // Pretend we have a big monitor
        //  THE FAKE ID: This tricks Cloudflare into thinking you are a regular person on a desktop.
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.setExperimentalOption("excludeSwitches", java.util.Collections.singletonList("enable-automation")); // Turns off the "Chrome is being controlled" banner
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--incognito");
        // ⚡ THE EAGER STRATEGY: Stop waiting once the basic HTML is loaded!
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);


        WebDriver driver = new ChromeDriver(options);
        try {
            // 2. Visit the site and wait for Cloudflare
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.get(url);

            // Wait up to 20 seconds, but continue the INSTANT the title is no longer "Just a moment"
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.not(ExpectedConditions.titleContains("Just a moment")));


            // 3. Extract the final rendered HTML and convert it back to a JSoup Document
            String html = driver.getPageSource();
            return Jsoup.parse(html);
        } finally {
            // 4. CRITICAL: Always close the browser or your RAM will fill up!
            driver.quit();
        }
    }


}
