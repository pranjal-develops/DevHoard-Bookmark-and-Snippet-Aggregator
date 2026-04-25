package com.devhoard.service;

import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.repository.BookmarkRepo;
import com.devhoard.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.HttpStatusException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepo bookmarkRepo;
    private final UserRepo userRepo;

    @Async
    public void scrapeAndSave(String url, Set<String> categories, String guestId, String username) {
        Document document = null;
        String scrapeUrl = url;

        if (url.contains("reddit.com") && !url.contains("old.reddit.com")) {
            scrapeUrl = url.replace("www.reddit.com", "old.reddit.com");
        }

        try {
            try {
                // Initial Jsoup attempt
                document = Jsoup.connect(scrapeUrl)
                        .userAgent(
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .referrer("https://www.google.com")
                        .get();
            } catch (IOException e) {
                // Selenium fallback
                document = scrapeWithSelenium(scrapeUrl);
            }

            if (document == null)
                throw new Exception("Failed to resolve document");

            String title = document.title();
            String imgUrl = findImageUrl(document);

            if (imgUrl == null || imgUrl.isEmpty()) {
                document = scrapeWithSelenium(scrapeUrl);
                imgUrl = findImageUrl(document);
                title = document.title();
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
            throw new RuntimeException("Scrape failed for: " + url, e);
        }
    }

    public void deleteBookmark(Long id, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found: " + id));
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
                .orElseThrow(() -> new RuntimeException("Bookmark not found: " + id));
        verifyOwnership(bookmark, username, guestId);
        bookmark.setCategories(categories);
        return bookmarkRepo.save(bookmark);
    }

    public Bookmark toggleFavorite(Long id, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found: " + id));
        verifyOwnership(bookmark, username, guestId);
        bookmark.setFavorite(!bookmark.isFavorite());
        return bookmarkRepo.save(bookmark);
    }

    public List<Bookmark> getFavorites(String username, String guestId) {
        return bookmarkRepo.findFavoritesByOwner(username, guestId);
    }

    private static String firstNonEmpty(Document doc, String... cssQueries) {
        for (String q : cssQueries) {
            String v = doc.select(q).attr(q.contains("meta") ? "abs:content" : "abs:href");
            if (v != null && !v.isEmpty())
                return v;
        }
        return "";
    }

    private static String findImageUrl(Document doc) {
        String[] metaQueries = {
                "meta[property=og:image:secure_url]", "meta[property=og:image:url]",
                "meta[property=og:image]", "meta[name=twitter:image]",
                "meta[itemprop=image]", "link[rel=image_src]"
        };

        for (String q : metaQueries) {
            String v = doc.select(q).attr(q.contains("meta") ? "abs:content" : "abs:href");
            if (v != null && !v.isEmpty())
                return v;
        }

        for (org.jsoup.nodes.Element img : doc.select(".s-prose img, #question img, main img, article img")) {
            String v = img.attr("abs:src");
            if (isValidImage(v))
                return v;
        }

        String loc = doc.location().toLowerCase();
        if (loc.contains("stackoverflow.com"))
            return "https://cdn.sstatic.net/Sites/stackoverflow/Img/apple-touch-icon@2.png";
        if (loc.contains("reddit.com"))
            return "https://www.redditstatic.com/desktop2x/img/favicon/android-icon-192x192.png";

        return "";
    }

    private static boolean isValidImage(String url) {
        if (url == null || url.isEmpty())
            return false;
        String lower = url.toLowerCase();
        return !(lower.contains("icon") || lower.contains("favicon") ||
                lower.contains("logo") || lower.contains("76x76") || lower.contains("px-"));
    }

    private Document scrapeWithSelenium(String url) {
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium-browser");
        options.addArguments("--headless=new", "--disable-dev-shm-usage", "--no-sandbox", "--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled", "--window-size=1920,1080", "--incognito");
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--single-process");
        options.addArguments("--disable-extensions");
        options.addArguments("--blink-settings=imagesEnable=false");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(url);
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < 12000) {
                String title = driver.getTitle();
                String source = driver.getPageSource();

                if (title != null && !title.isEmpty() &&
                        !title.toLowerCase().contains("loading") &&
                        source.contains("<meta")) {
                    break;
                }
                Thread.sleep(1000);
            }

            return Jsoup.parse(driver.getPageSource(), url);
        } catch (InterruptedException e) {
            throw new RuntimeException("Selenium scrape interrupted", e);
        } finally {
            driver.quit();
        }
    }

    private void verifyOwnership(Bookmark bookmark, String username, String guestId) {
        if (bookmark.getUser() != null) {
            if (username == null || !bookmark.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Unauthorized");
            }
        } else if (bookmark.getGuestId() == null || !bookmark.getGuestId().equals(guestId)) {
            throw new RuntimeException("Unauthorized");
        }
    }
}
