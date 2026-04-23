# 🚀 DevHoard
**The Smart Bookmark & Snippet Aggregator for Developers**

DevHoard is a high-performance, full-stack web archival system designed for developers. It allows users to instantly save, search, and manage technical resources with an automated hybrid scraping engine that extracts metadata, images, and descriptions in real-time.

---

## 🎨 Design Aesthetic: "Cyber-Minimalist"
DevHoard features a custom-built, premium UI designed for high-focus development environments:
- **Dark Mode First**: High-contrast, easy-on-the-eyes "Enter Cyber" mode.
- **Glassmorphism**: Backdrop blurs and subtle glows for depth and focus.
- **Micro-Animations**: Real-time feedback for archival operations (Toasts, Pulsing states).

---

## 💻 Tech Stack
- **Backend (API)**: Java 21, Spring Boot 4.0.4, Spring Data JPA, Spring Security (JWT).
- **Data Extraction**: Hybrid Engine (Jsoup + Selenium WebDriver) to handle both static HTML and JS-heavy/Cloudflare-protected sites.
- **Database**: PostgreSQL 15.
- **Frontend (SPA)**: React 19, TypeScript, Redux Toolkit, Tailwind CSS v4, Vite.
- **Infrastructure**: Docker & Docker Compose.

---

## ⚙️ Core Architectural Highlights

### 1. Hybrid Scraping Strategy
DevHoard uses a two-stage archival pipeline:
1. **Jsoup (L1)**: Ultra-fast static parsing for documented sources.
2. **Selenium (L2)**: Fallback headless browser execution for dynamic content and anti-bot bypass.

### 2. Stateless JWT Authentication
Implemented a robust authentication layer using JSON Web Tokens. Security is managed via a custom `JwtAuthenticationFilter` ensuring zero server-side session overhead and high scalability.

### 3. Guest-to-User Identity Consolidation
Features a "Try before you buy" mode. Guest users can archive bookmarks using a local session ID; upon registration or login, the system automatically migrates all guest-session records to the new authenticated account.

---

## 🛠️ Local Setup Guide

### Prerequisites
- Docker & Docker Compose
- Node.js (for local frontend development, optional)

### 🚀 Running the Full Stack
The easiest way to start DevHoard is via Docker Compose. This starts the Database, API, and UI simultaneously:

1. **Clone the repository.**
2. **Create a `.env` file** in the root directory (copy from `.env.example` if available).
3. **Run the stack:**
```bash
docker-compose up --build
```
- **Frontend**: `http://localhost:80` (or `http://localhost:5173` in dev mode)
- **Backend API**: `http://localhost:8080`

---

## 🔮 Future Roadmap
- **AI-Powered Tagging**: Automatically categorize links using LLM integration (Gemini/ChatGPT).
- **Content Diffing**: Periodically monitor archived pages and notify users of content changes.
- **Distributed Fleet**: Move the scraping logic to a separate microservice with a message broker (RabbitMQ/Kafka).

---
