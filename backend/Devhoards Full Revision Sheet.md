# Devhoard's fUll revision sheet

This document serves as the ultimate technical reference and revision guide for the **DevHoard** project—a full-stack web archival system. It covers every architectural decision, pattern, and engineering hurdle encountered during development.

---

## 🏗️ Project Overview
DevHoard is a high-performance web bookmarking and archival system. It allows users (and guests) to submit URLs, which the backend then "scrapes" to extract metadata (titles, descriptions, images). It features a "Try before you buy" guest mode, identity consolidation, and a responsive, cyber-minimalist UI.

## 🏛️ Architecture & Design Decisions
- **Stateless Full-Stack**: Deployed as independent Backend (API) and Frontend (SPA) services.
- **RESTful API**: Communication via JSON over HTTP.
- **Hybrid Scraping Engine**: A multi-layered extraction strategy (Jsoup + Selenium).
- **Identity Consolidation Pattern**: Logic to migrate "Guest" data to "Authenticated" accounts.
- **Micro-UX Approach**: Focus on real-time feedback (Toasts) and reactive UI updates (`updateSignal`).

| Decision | Implementation | Why it matters |
| :--- | :--- | :--- |
| **Auth** | Stateless JWT | Allows horizontal scaling; no server-side session overhead. |
| **Database** | PostgreSQL | Robust relational data handling with support for complex JPQL queries. |
| **Scraping** | Jsoup -> Selenium | Balances speed (Jsoup) with capability (Selenium for JS-heavy sites). |
| **State** | Redux Toolkit | Centralized source of truth for UI, Auth, and Bookmark states. |

## 🛠️ Tech Stack & Versions
- **Backend**: Java 21, Spring Boot 4.0.4, Maven, Spring Data JPA, Spring Security, Selenium 4.41, Jsoup 1.17.
- **Frontend**: React 19.x, TypeScript, Redux Toolkit, Tailwind CSS v4, Axios, Vite.
- **Infrastructure**: Docker, Docker Compose, PostgreSQL 15-Alpine.

---

## 🚀 Setup & Run Instructions

### 1. Database & Infrastructure
```bash
# Start PostgreSQL via Docker
docker-compose up db -d
```

### 2. Backend Development
```bash
cd backend
mvn clean install      # Dependency resolution
mvn spring-boot:run    # Start server on port 8080
```

### 3. Frontend Development
```bash
cd frontend
npm install            # Install dependencies
npm run dev            # Start Vite server on port 5173
```

---

## 🍃 Backend (Spring Boot)

### 📦 Project Structure
- `com.devhoard.controller`: Entry points for HTTP requests (REST Controllers).
- `com.devhoard.service`: Pure business logic (Scraping engine, User migration).
- `com.devhoard.repository`: Data access layer (Spring Data JPA).
- `com.devhoard.entities`: Database models (User, Bookmark).
- `com.devhoard.security`: Authentication filter chain and JWT utilities.
- `com.devhoard.DTO`: Data Transfer Objects (Request/Response models).

### 🧠 Key Concepts Learned

#### 1. Dependency Injection (DI)
- **Definition**: Passing dependencies into a class rather than the class creating them itself.
- **Example**: `@Autowired private BookmarkService bookmarkService;`
- **Why**: Facilitates loose coupling and makes unit testing significantly easier.

#### 2. Persistence & JPQL
- **Definition**: Using Java Persistence API to map objects to database tables.
- **Example**: 
```java
@Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId")
List<Bookmark> findByUserId(@Param("userId") Long userId);
```
- **Why**: Allows querying data using Java objects instead of raw SQL strings.

#### 3. Identity Consolidation Logic
- **Definition**: Merging anonymous session data (GuestID) into a hard-principal account (User) upon login.
- **Example**: 
```java
// Logic to migrate bookmarks from GuestID to User object
guestBookmarks.forEach(b -> b.setUser(authenticatedUser));
bookmarkRepo.saveAll(guestBookmarks);
```
- **Why**: Crucial for UX; allows users to "test" the app without losing data once they register.

#### 4. Asynchronous Processing (@Async)
- **Definition**: Running heavy tasks (like Selenium scraping) on a separate thread.
- **Example**: `@Async public void scrapeUrl(String url) { ... }`
- **Why**: Prevents the user's HTTP request from hanging while the browser loads the page.

### 🔐 Security: Auth Flow & JWT
- **Statelessness**: The server does not store a "Session." It only trusts the `Authorization: Bearer <JWT>` header.
- **The Filter Chain**: Every request is intercepted by `JwtAuthenticationFilter` to validate the token before reaching the controller.

---

## ⚛️ Frontend (React)

### 🧠 Key Concepts Learned

#### 1. Redux Toolkit (State Management)
- **Definition**: A predictable state container for managing global app data.
- **Example**: `const { user } = useSelector((state: RootState) => state.auth);`
- **Why**: Prevents "Prop Drilling" (passing data through 5 layers of components).

#### 2. Custom Hooks (Logic Encapsulation)
- **Definition**: Extracting complex logic (like scraping polling) into reusable functions.
- **Example**: `const { handleSubmit } = useBookmarks();`
- **Why**: Keeps components clean and focused solely on the UI/Rendering.

#### 3. Axios Interceptors
- **Definition**: Global logic that runs before *every* request or after *every* response.
- **Example**: 
```typescript
// Auto-injecting JWT token into headers
api.interceptors.request.use(config => {
    config.headers.Authorization = `Bearer ${token}`;
    return config;
});
```
- **Why**: Ensures security headers are never forgotten and handles 401 (Unauthorized) errors globally.

---

## 🌓 Full-stack Integration

### 📑 Integration Cheat-Sheet
- **CORS**: Configured in `SecurityConfig.java` to allow port `5173` (Frontend) to talk to port `8080` (Backend).
- **Environment Variables**: 
    - Frontend: Uses `.env` (prefixed with `VITE_`).
    - Backend: Uses `application.properties` or OS environment variables.
- **Vite Proxy**: Configured in `vite.config.ts` to solve dev-time CORS issues.

### 🛠️ Troubleshooting Checklist
1. **Got a 403 Forbidden?**: Check `SecurityConfig` matchers or the expired JWT token.
2. **Scraper returning blank?**: Check the User-Agent string or if Selenium is reaching a Cloudflare wall.
3. **Frontend not seeing updates?**: Verify the `updateSignal` in Redux is being toggled after the POST request.

---

## 🐳 Docker & Productivity

### 📋 Useful Docker Commands
- `docker-compose up -d`: Run the entire stack in the background.
- `docker-compose logs -f backend`: View real-time server logs.
- `docker exec -it DevhoadDB psql -U postgres`: Jump into the database CLI.

### 📋 CLI Productivity
- `mvn clean compile`: Verify there are no syntax errors in your Java code.
- `npm run build`: Check if your TypeScript types are correct before deployment.

---

## ⚠️ Mistakes, Gotchas & Lessons Learned

| Issue | Probable Cause | Fix / Learning |
| :--- | :--- | :--- |
| **Selenium Memory Leak** | Forgetting `driver.quit()` | Always use a `finally { driver.quit() }` block to kill the Chrome process. |
| **CORS Errors** | Mismatched Ports | Ensure `allowedOrigins` in Backend exactly matches the Frontend URL. |
| **Alpine Selenium Crash** | Missing system libs | Alpine needs `libstdc++` and `udev` installed to run Chromium. |
| **Scraping 403/401** | Missing User-Agent | Modern sites block requests that look like "bots." Always spoof a real browser. |

---

## 📈 Further Improvements & Next Steps
1. **Implement Redis Caching**: Store scraped metadata in RAM to avoid re-scraping the same URL within 24 hours. (Priority: High)
2. **OAuth2 Integration**: Allow users to "Sign in with Google/GitHub" to reduce friction. (Priority: Medium)
3. **Full e2e Testing**: Write Playwright tests to simulate a user adding a bookmark from end-to-end. (Priority: Medium)

---

## 📚 Appendix

### ⌨️ Cheat-Sheet
- **Git**: `git commit -am "msg"`, `git push origin main`.
- **Maven**: `mvn clean package` (Creates the deployable JAR).
- **NPM**: `npm run dev` (Local dev), `npm run build` (Production build).

### ❓ Revision Exercises
1. **Q**: Difference between `onToggleFavorite` props and Redux state?
   - **A**: Props are for local UI updates; Redux is for shared global state.
2. **Q**: Why do we use `@Async` for the scraper?
   - **A**: To prevent blocking the main thread, allowing the user to continue browsing while the scraping happens.
3. **Q**: What is the purpose of `e.stopPropagation()` in the Card?
   - **A**: To prevent a click on a button (like "Favorite") from also triggering the "Open URL" event on the parent card.

---
**End of Revision Sheet. Ready for Deployment.**
