package com.devhoard.service; // Package declaration for service layer organization

// Internal domain models and data access layers
import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.repository.BookmarkRepo;
import com.devhoard.repository.UserRepo;

// Lombok for standard boilerplate reduction
import lombok.RequiredArgsConstructor;

// Jsoup for lightweight, server-side DOM parsing and HTTP connectivity
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// Spring framework components for dependency injection, asynchronous task execution, and security context
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// Scraping specific imports: Selenium for dynamic content rendering and Jsoup for static parsing
import org.jsoup.HttpStatusException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.PageLoadStrategy;

// Standard Java utilities for duration handling and collections
import java.time.Duration;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Service class responsible for bookmark management and metadata extraction.
 * Implements a dual-scraping strategy: prioritized Jsoup for speed with a
 * Selenium fallback for SPAs.
 */
@Service
public class BookmarkService {

    // Final dependency fields to ensure safe concurrency and testability
    private final BookmarkRepo bookmarkRepo;
    private final UserRepo userRepo;

    /**
     * Constructor-based dependency injection is preferred over @Autowired on
     * fields.
     */
    public BookmarkService(BookmarkRepo bookmarkRepo, UserRepo userRepo) {
        this.bookmarkRepo = bookmarkRepo;
        this.userRepo = userRepo;
    }

    /**
     * Orchestrates the scraping and persistence of a bookmark.
     * Marked @Async to prevent blocking the main request thread during long-running
     * browser operations.
     */
    @Async
    public void scrapeAndSave(String url, Set<String> categories, String guestId, String username) {
        System.out.println("[Scraper] Initiating scrape for: " + url);
        Document document = null;

        // Hostname normalization: reddit.com is notoriously difficult for basic bots;
        // old.reddit offers better static HTML.
        String scrapeUrl = url;
        if (url.contains("reddit.com") && !url.contains("old.reddit.com")) {
            scrapeUrl = url.replace("www.reddit.com", "old.reddit.com");
        }

        try {
            /*
             * Historical Context:
             * Initial implementation used standard Jsoup connections. This resulted in 403
             * Forbidden errors
             * on major platforms (StackOverflow, YouTube) due to missing Browser-standard
             * headers.
             */

            // Phase 1: Fast Static Scrape
            try {
                document = Jsoup.connect(scrapeUrl)
                        .userAgent(
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .referrer("https://www.google.com")
                        .get();
            } catch (IOException e) {
                // Secondary Strategy: Selenium failover for JS-heavy or bot-protected sites
                System.out.println("[Scraper] Fast-scrape failed. Triggering Selenium failover for: " + scrapeUrl);
                document = scrapeWithSelenium(scrapeUrl);
            }

            if (document == null)
                throw new Exception("Document resolution failed after all strategies attempted.");

            String title = document.title();

            // Diagnostic logging to verify DOM consistency after extraction
            String htmlPreview = document.html().substring(0, Math.min(500, document.html().length()));
            System.out.println("[Scraper Trace] Extracted Title: [" + title + "]");
            System.out.println("[Scraper Trace] Rendered HTML Fragment: " + htmlPreview);

            // Metadata extraction: Prioritizing Open Graph and Twitter card data
            String imgUrl = findImageUrl(document);

            // Visual validation: If metadata is missing, the page may require a full
            // browser render for lazy-loaded assets.
            if (imgUrl == null || imgUrl.isEmpty()) {
                System.out.println("[Scraper] High-quality imagery missing. Re-attempting visual render...");
                document = scrapeWithSelenium(scrapeUrl);
                imgUrl = findImageUrl(document);
                title = document.title(); // Overwriting any JS-based placeholder title found in phase 1
            }

            // Description extraction: Hierarchical search through standard snippet tags
            String description = firstNonEmpty(document,
                    "meta[name=description]",
                    "meta[property=og:description]",
                    "meta[name=twitter:description]",
                    "meta[itemprop=description]");

            // Entity instantiation
            Bookmark bookmark = new Bookmark(url, title, description, imgUrl, categories);

            // Context-aware ownership mapping
            if (username != null) {
                User user = userRepo.findByUsername(username).orElse(null);
                bookmark.setUser(user);
            } else {
                bookmark.setGuestId(guestId);
            }

            bookmarkRepo.save(bookmark); // Final repository persistence
            System.out.println("[Scraper] Data successfully synchronized for: " + title);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Synchronized write failed for URL: " + url + ". Error: " + e.getMessage(), e);
        }
    }

    /**
     * Standard deletion logic with identity validation.
     */
    public void deleteBookmark(Long id, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity resolution failed for ID: " + id));
        verifyOwnership(bookmark, username, guestId);
        bookmarkRepo.deleteById(id);
    }

    /**
     * Search implementation delegating to the repository tier.
     */
    public List<Bookmark> search(String keyword, String username, String guestId) {
        return bookmarkRepo.searchByOwner(username, guestId, keyword);
    }

    public List<Bookmark> getAll(String username, String guestId) {
        return bookmarkRepo.findByOwner(username, guestId);
    }

    public List<Bookmark> getByCategory(String category, String username, String guestId) {
        return bookmarkRepo.findByCategoryAndOwner(username, guestId, category);
    }

    /**
     * State mutation for categorized tags.
     */
    public Bookmark updateCategory(Long id, Set<String> categories, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity resolution failed for ID: " + id));
        verifyOwnership(bookmark, username, guestId);
        bookmark.setCategories(categories);
        return bookmarkRepo.save(bookmark);
    }

    /**
     * Toggles the importance flag (Favorite) status.
     */
    public Bookmark toggleFavorite(Long id, String username, String guestId) {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity resolution failed for ID: " + id));
        verifyOwnership(bookmark, username, guestId);
        bookmark.setFavorite(!bookmark.isFavorite());
        return bookmarkRepo.save(bookmark);
    }

    /**
     * Retrieves favorited entities for the specified owner.
     */
    public List<Bookmark> getFavorites(String username, String guestId) {
        return bookmarkRepo.findFavoritesByOwner(username, guestId);
    }

    /**
     * Utility method to extract the first available attribute from a list of CSS
     * selectors.
     */
    private static String firstNonEmpty(Document doc, String... cssQueries) {
        for (String q : cssQueries) {
            // Absolute attribute extraction to resolve relative paths to fully-qualified
            // URLs
            String v = doc.select(q).attr(q.contains("meta") ? "abs:content" : "abs:href");
            if (v != null && !v.isEmpty())
                return v;
        }
        return "";
    }

    /**
     * Advanced image discovery algorithm utilizing structural and metadata
     * heuristics.
     */
    private static String findImageUrl(Document doc) {
        // Strategy 1: Targeted Meta extraction (Standardized Social tags)
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
            if (v != null && !v.isEmpty())
                return v;
        }

        // Strategy 2: Exhaustive Meta scan for common image keywords
        for (org.jsoup.nodes.Element meta : doc.select("meta")) {
            String property = meta.attr("property").toLowerCase();
            String name = meta.attr("name").toLowerCase();

            if (property.contains("image") || name.contains("image") ||
                    property.contains("thumbnail") || name.contains("thumbnail")) {

                String v = meta.attr("abs:content");
                if (isValidImage(v))
                    return v;
            }
        }

        // Strategy 3: Structural DOM discovery targeting primary content containers
        for (org.jsoup.nodes.Element img : doc.select(".s-prose img, #question img, main img, article img")) {
            String v = img.attr("abs:src");
            if (isValidImage(v))
                return v;
        }

        // Strategy 4: Hardcoded branding fallbacks for legacy or high-protection sites
        String loc = doc.location().toLowerCase();
        if (loc.contains("stackoverflow.com")) {
            return "https://cdn.sstatic.net/Sites/stackoverflow/Img/apple-touch-icon@2.png";
        }
        if (loc.contains("reddit.com")) {
            return "https://www.redditstatic.com/desktop2x/img/favicon/android-icon-192x192.png";
        }

        return "";
    }

    /**
     * Validates image quality to avoid UI elements and small favicons.
     */
    private static boolean isValidImage(String url) {
        if (url == null || url.isEmpty())
            return false;
        String lower = url.toLowerCase();

        // Heuristic filtering: Exclusion of common UI asset keywords
        return !(lower.contains("icon") || lower.contains("favicon") ||
                lower.contains("logo") || lower.contains("76x76") ||
                lower.contains("px-"));
    }

    /**
     * Full-browser scraping implementation using headless Chromium.
     * Necessary for sites requiring JS execution to populate the DOM with metadata.
     */
    private Document scrapeWithSelenium(String url) {
        long startTime = System.currentTimeMillis();

        // Local environment driver resolution
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        // Browser instance configuration
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium-browser");
        options.addArguments("--headless=new"); // Modern headless engine execution
        options.addArguments("--disable-dev-shm-usage"); // Mitigate Docker shared memory exhaustion
        options.addArguments("--no-sandbox"); // Required for Alpine/Docker root execution
        options.addArguments("--disable-blink-features=AutomationControlled"); // Masking automation detection
        options.addArguments("--window-size=1920,1080");

        // Stealth profile: Mimicking a high-entropy desktop user agent
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        options.setExperimentalOption("excludeSwitches", java.util.Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        options.addArguments("--disable-gpu");
        options.addArguments("--blink-settings=imagesEnabled=true");
        options.addArguments("--incognito");
        options.addArguments("--disable-extensions");

        // Strategy refinement: Immediate control return to avoid waiting for
        // non-essential assets
        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        WebDriver driver = new ChromeDriver(options);
        try {
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

            // Resource Monitor: Custom wait loop to stop execution as soon as 'real'
            // content is identified
            while (System.currentTimeMillis() - startTime < 12000) {
                String title = driver.getTitle();
                String source = driver.getPageSource();

                /*
                 * Content Stability Detection:
                 * Stop once the title is meaningful and metadata tags have been injected by the
                 * JS engine.
                 */
                if (title != null && !title.isEmpty() &&
                        !title.toLowerCase().contains("untitled") &&
                        !title.toLowerCase().contains("loading") &&
                        source.contains("<meta")) {

                    System.out.println("[Selenium] Content stability reached for: " + title);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.stop();");
                    break;
                }
                Thread.sleep(1000); // Polling frequency set to 1Hz
            }

            // new WebDriverWait(driver, Duration.ofSeconds(10))
            // .until(d -> !d.getTitle().isEmpty()); // Stop as soon as the title exists!

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }

            String html = driver.getPageSource();
            return Jsoup.parse(html, url);
        } catch (InterruptedException e) {
            throw new RuntimeException("Async interruption during Selenium operation: " + e.getMessage(), e);
        } finally {
            // JVM Resource cleanup: Chromium processes must be terminated to prevent memory
            // leakage
            driver.quit();
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[Scraper] Lifecycle completed in " + duration + "ms for: " + url);
        }
    }

    /**
     * Perimeter security check for entity ownership.
     */
    private void verifyOwnership(Bookmark bookmark, String username, String guestId) {
        if (bookmark.getUser() != null) {
            if (username == null || !bookmark.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Access Denied: Resource ownership mismatch.");
            }
        } else if (bookmark.getGuestId() == null || !bookmark.getGuestId().equals(guestId)) {
            throw new RuntimeException("Access Denied: Identity token mismatch.");
        }
    }
}
