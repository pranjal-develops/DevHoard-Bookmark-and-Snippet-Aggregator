# 🚀 DevHoard
**The Smart Bookmark & Snippet Aggregator for Developers**

DevHoard is a full-stack web application designed for developers to instantly save, search, and manage technical articles and documentation. It features an automated backend web-scraping engine that dynamically extracts metadata from any saved URL.


---

## 💻 Tech Stack
*   **Backend Server:** Java 25, Spring Boot 3
*   **Database & ORM:** PostgreSQL, Spring Data JPA, Hibernate
*   **Data Extraction:** JSoup (HTML Parser)
*   **Frontend Client:** React 19, TypeScript, Tailwind CSS v4, Vite, Axios
*   **Architecture:** RESTful API -> Single Page Application (SPA)

---

## ⚙️ Core Architectural Highlights

### 1. Automated Metadata Extraction (Web Scraping)
Instead of forcing users to manually type titles and find images for their bookmarks, the Spring Boot backend uses **JSoup** to automatically fetch and parse the target URL's DOM. 
*   **Anti-Bot Spoofing:** To bypass basic anti-bot firewalls on strict websites, the scraping engine dynamically injects custom `User-Agent` and `Accept-Language` HTTP headers to successfully mimic a human using Google Chrome.

### 2. Spring Data JPA Dynamic Query Generation
The core search functionality is built using JPA's derived query methods (`findByTitleContainingIgnoreCase`). This allows the backend engine to perform real-time, case-insensitive `LIKE %query%` SQL filtering without requiring manually written SQL statements, ensuring clean and maintainable repository code.

### 3. Modern React Componentization
The frontend UI is strictly componentized with dedicated TypeScript interfaces (`UrlFormProps`, `SearchProps`, etc.) to guarantee end-to-end type safety between the Tailwind layout and the Axios network payloads.

---

## 🛠️ Local Setup Guide

### Prerequisites
*   Java JDK 25+
*   Node.js 20+
*   PostgreSQL installed and running locally

### 1. Database Configuration
Create a new, empty database in PostgreSQL named `devhoard`. 
Update the credentials in `backend/src/main/resources/application.yaml`:
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/devhoard
  username: YOUR_USERNAME_HERE
  password: ${DB_PASSWORD:password}
```

### 2. Run the Spring Boot API
Navigate to the backend folder and start the server. It will automatically build the SQL tables (Auto-DDL) and run on Port 8080:
```bash
cd backend
./mvnw spring-boot:run
```

### 3. Run the React Dashboard
Open a new terminal, navigate to the frontend folder, and start the Vite dev server:
```bash
cd frontend
npm install
npm run dev
```
Navigate to `http://localhost:5173` in your browser.

---

## 🔮 Future Roadmap
*   **Cloudflare Bypass:** Upgrade the JSoup HTML parser to an automated Headless Browser (Selenium WebDriver) to execute JavaScript challenges and scrape sites protected by Cloudflare.
*   **Authentication:** Implement Spring Security with JSON Web Tokens (JWT) to support multi-user accounts and private bookmark hoards.
