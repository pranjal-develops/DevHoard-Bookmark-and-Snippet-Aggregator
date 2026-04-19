package com.devhoard.service;

import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.repository.BookmarkRepo;
import com.devhoard.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Set;

@Service
// @RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepo bookmarkRepo;
    private final UserRepo userRepo;

    public BookmarkService(BookmarkRepo bookmarkRepo, UserRepo userRepo) {
        this.bookmarkRepo = bookmarkRepo;
        this.userRepo = userRepo;
    }

    @Async
    public void scrapeAndSave(String url, Set<String> categories, String guestId, String username) {
        System.out.println("あ! [Scraper] Starting to scrape " + url);
        Document document = null;
        // Use Old Reddit for scraping (it has better metadata for bots)
        String scrapeUrl = url;
        if (url.contains("reddit.com") && !url.contains("old.reddit.com")) {
            scrapeUrl = url.replace("www.reddit.com", "old.reddit.com");
        }

        try {
            // Some Sites like StackOverflow will block this as when JSoup makes an HTTP
            // request to StackOverflow, it sends a secret header identifying itself as
            // "Java/17.0.x".
            // StackOverflow's firewall immediately sees that you are a bot, not a human,
            // and blocks your request with a 403 Forbidden status. Because JSoup crashes on
            // a 403, your try/catch block catches the crash, throws your custom
            // RuntimeException, and the entity is never saved.
            // Document document = Jsoup.connect(url).get();

            // This will "spoof" your identity so StackOverflow or any other such site
            // thinks you are a real person using the Google Chrome browser!
            // Document document = Jsoup.connect(url)
            // .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
            // (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            // .referrer("http://www.google.com")
            // .get();
            try {
                document = Jsoup.connect(scrapeUrl)
                        .userAgent(
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .referrer("https://www.google.com")
                        .get();
            } catch (IOException e) {
                document = scrapeWithSelenium(scrapeUrl);
            }
            if (document == null)
                throw new Exception("Failed to Scrape");

            String title = document.title();
            
            // 🕵️‍♂️ LOUD PREVIEW: Let's see what we actually got!
            String htmlPreview = document.html().substring(0, Math.min(500, document.html().length()));
            System.out.println("あ! [Scraper Trace] Title: [" + title + "]");
            System.out.println("あ! [Scraper Trace] HTML Preview: " + htmlPreview);

            String imgUrl = findImageUrl(document);

            // 🕵️‍♂️ THE FALLBACK RESCUE
            if (imgUrl == null || imgUrl.isEmpty()) {
                System.out.println("⚠️ [Scraper] Image missing in initial scrape. Attempting visual render...");
                document = scrapeWithSelenium(scrapeUrl);
                imgUrl = findImageUrl(document);
                title = document.title(); // Refresh title too
            }

            String description = firstNonEmpty(document, 
                "meta[name=description]", 
                "meta[property=og:description]", 
                "meta[name=twitter:description]",
                "meta[itemprop=description]");
            
            Bookmark bookmark = new Bookmark(url, title, description, imgUrl, categories);

            if (username != null) {
                User user = userRepo.findByUsername(username).orElse(null);
                bookmark.setUser(user);
            } else {
                bookmark.setGuestId(guestId);
            }

            bookmarkRepo.save(bookmark);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while saving the entity" + e.getMessage(), e);
        }
    }

    public void deleteBookmark(Long id, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        verifyOwnership(bookmark, username, guestId);
        bookmarkRepo.deleteById(id);
    }

    public List<Bookmark> search(String keyword, String username, String guestId) {
        return bookmarkRepo.searchByOwner(username, guestId, keyword);
    }

    public List<Bookmark> getAll(String username, String guestId) {
        return bookmarkRepo.findByOwner(username, guestId);
    }

    public List<Bookmark> getByCategory(String category, String username, String guestId) {
        return bookmarkRepo.findByCategoryAndOwner(username, guestId, category);
    }

    public Bookmark updateCategory(Long id, Set<String> categories, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        verifyOwnership(bookmark, username, guestId);
        bookmark.setCategories(categories);
        return bookmarkRepo.save(bookmark);
    }

    public Bookmark toggleFavorite(Long id, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        verifyOwnership(bookmark, username, guestId);
        bookmark.setFavorite(!bookmark.isFavorite());
        return bookmarkRepo.save(bookmark);
    }

    // Add a helper for the controller too
    public List<Bookmark> getFavorites(String username, String guestId) {
        return bookmarkRepo.findFavoritesByOwner(username, guestId);
    }

    // private static String firstNonEmpty(Document doc, String... cssQueries) {
    // for (String q : cssQueries) {
    // String v = doc.select(q).attr(q.contains("meta") ? "abs:content" :
    // "abs:href");
    // if (v != null && !v.isEmpty()) return v;
    // }
    // return "";
    // }

    private static String firstNonEmpty(Document doc, String... cssQueries) {
        for (String q : cssQueries) {
            String v = doc.select(q).attr(q.contains("meta") ? "abs:content" : "abs:href");
            if (v != null && !v.isEmpty())
                return v;
        }
        return "";
    }

    private static String findImageUrl(Document doc) {
        // Stage 1: Specific high-quality meta tags
        String[] metaQueries = {
            "meta[property=og:image:secure_url]", 
            "meta[property=og:image:url]", 
            "meta[property=og:image]",
            "meta[name=twitter:image]", 
            "meta[itemprop=image]", 
            "link[rel=image_src]"
        };
        
        for (String q : metaQueries) {
            String v = doc.select(q).attr(q.contains("meta") ? "abs:content" : "abs:href");
            if (v != null && !v.isEmpty()) return v;
        }

        // Stage 2: The Shotgun meta-scan
        for (org.jsoup.nodes.Element meta : doc.select("meta")) {
            String property = meta.attr("property").toLowerCase();
            String name = meta.attr("name").toLowerCase();

            if (property.contains("image") || name.contains("image") ||
                    property.contains("thumbnail") || name.contains("thumbnail")) {

                String v = meta.attr("abs:content");
                if (isValidImage(v)) return v;
            }
        }

        // Stage 3: THE CONTENT SCAN
        for (org.jsoup.nodes.Element img : doc.select(".s-prose img, #question img, main img, article img")) {
            String v = img.attr("abs:src");
            if (isValidImage(v)) return v;
        }

        // Stage 4: BRANDING FALLBACK
        String loc = doc.location().toLowerCase();
        if (loc.contains("stackoverflow.com")) {
            return "https://cdn.sstatic.net/Sites/stackoverflow/Img/apple-touch-icon@2.png";
        }
        if (loc.contains("reddit.com")) {
            return "https://www.redditstatic.com/desktop2x/img/favicon/android-icon-192x192.png";
        }

        return "";
    }

    // THE QUALITY GUARD:
    private static boolean isValidImage(String url) {
        if (url == null || url.isEmpty())
            return false;
        String lower = url.toLowerCase();

        return !(lower.contains("icon") || lower.contains("favicon") ||
                lower.contains("logo") || lower.contains("76x76") ||
                lower.contains("px-"));
    }

    private Document scrapeWithSelenium(String url) {
        long startTime = System.currentTimeMillis(); // Start the stopwatch

        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        // 1. Setup the invisible Chrome driver
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium-browser");
        options.addArguments("--headless=new"); // Runs without a window & Use the 'new' headless engine
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled"); // Hides the "I am a robot" flag
        options.addArguments("--window-size=1920,1080"); // Pretend we have a big monitor
        // THE FAKE ID: This tricks Cloudflare into thinking you are a regular person on
        // a desktop.
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.setExperimentalOption("excludeSwitches", java.util.Collections.singletonList("enable-automation")); // Turns
                                                                                                                    // off
                                                                                                                    // the
                                                                                                                    // "Chrome
                                                                                                                    // is
                                                                                                                    // being
                                                                                                                    // controlled"
                                                                                                                    // banner
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--blink-settings=imagesEnabled=true");
        options.addArguments("--incognito");
        // Stop ads and tracking scripts from stealing your CPU/Time
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        // ⚡ THE EAGER STRATEGY: Stop waiting once the basic HTML is loaded!
        // options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        WebDriver driver = new ChromeDriver(options);
        try {
            // 2. Visit the site and wait for Cloudflare
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
            driver.get(url);

            // Wait up to 20 seconds, but continue the INSTANT the title is no longer "Just
            // a moment"
            // new WebDriverWait(driver, Duration.ofSeconds(120))
            // .until(ExpectedConditions.not(ExpectedConditions.titleContains("Just a
            // moment")));
            // .until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.tagName("body")));

            // Inside your scrapeWithSelenium method:
            driver.get(url); // This now returns INSTANTLY because of Strategy.NONE

            startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 12000) { // Increased to 12s for heavy SPAs
                String title = driver.getTitle();
                String source = driver.getPageSource();

                // 🔱 THE SUCCESS CONDITION: Title is present and NOT a placeholder
                if (title != null && !title.isEmpty() && 
                    !title.toLowerCase().contains("untitled") && 
                    !title.toLowerCase().contains("loading") &&
                    source.contains("<meta")) {
                    
                    System.out.println("✅ [Selenium] Reliable content detected: " + title);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.stop();");
                    break;
                }
                Thread.sleep(1000); // Wait 1s between checks
            }

            // new WebDriverWait(driver, Duration.ofSeconds(10))
            // .until(d -> !d.getTitle().isEmpty()); // Stop as soon as the title exists!

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }

            // 3. Extract the final rendered HTML and convert it back to a JSoup Document
            String html = driver.getPageSource();
            return Jsoup.parse(html, url);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 4. CRITICAL: Always close the browser or your RAM will fill up!
            driver.quit();
            long duration = System.currentTimeMillis() - startTime; // 🏁 Calculate
            System.out.println("あ! [Scraper] Successfully saved " + url + " in " + duration + "ms");
        }
    }

    private void verifyOwnership(Bookmark bookmark, String username, String guestId) {
        // 1. If it belongs to a User, the name must match
        if (bookmark.getUser() != null) {
            if (username == null || !bookmark.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Access Denied: Not your archive!");
            }
        }
        // 2. If it's a Guest bookmark, the guestId must match
        else if (bookmark.getGuestId() == null || !bookmark.getGuestId().equals(guestId)) {
            throw new RuntimeException("Access Denied: Identity mismatch!");
        }
    }

}
