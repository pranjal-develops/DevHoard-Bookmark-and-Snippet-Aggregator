# 🐳 Master Guide: Full-Stack Dockerization & Container Orchestration

This document distills the technical experience gained while containerizing the DevHoard system. It covers the deployment of a Spring Boot backend, a Vite/React frontend, and a PostgreSQL database using modern orchestration patterns.

---

## 🏗️ 1. Spring Boot Multi-Stage Architecture

To optimize for image size and build speed, we implemented a **Multi-Stage Build**. This separates the development environment (JDK + Maven) from the lean runtime (JRE).

```dockerfile
# Stage 1: The Factory (Maven + JDK)
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: The Distribution (Lightweight JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Key Learnings:
- **Cache-First Pattern**: Copying `pom.xml` before the `src` directory allows Docker to cache dependencies. If code changes but the `pom.xml` doesn't, Docker skips the expensive download phase.
- **Alpine Base**: Using `-alpine` versions of images significantly reduces the security attack surface and the final image footprint (down to ~150MB from ~600MB).

---

## 🔍 2. Selenium in Containerized Alpine

One of the biggest hurdles was running **Headless Chromium** on a lightweight Alpine OS. Alpine does not include several libraries required by modern browsers.

### The Dependency Mapping
The runtime image requires explicit installation of the browser and its drivers, along with specific system libraries.

```dockerfile
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    ca-certificates \
    libstdc++ \
    nss \
    freetype \
    ttf-dejavu \
    udev
```

### Path Awareness
Spring Boot needs to know exactly where the Chromium binary lives within the container. We solved this using Environment Variables in the `Dockerfile`:

```dockerfile
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_PATH=/usr/lib/chromium/
```

---

## 🤝 3. Orchestration & Healthchecks

In a microservice environment, the backend often crashes if the database isn't "Ready" (even if the container is "Running"). We solved this using **Docker Healthchecks**.

### The database healthcheck:
```yaml
db:
  image: postgres:15-alpine
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME} -d ${DB_NAME}"]
    interval: 5s
    timeout: 5s
    retries: 5
```

### The Backend dependency:
```yaml
backend:
  depends_on:
    db:
      condition: service_healthy # Vital: Wait for DB to be truly ready, not just starting
```

---

## ⚡ 4. Frontend: Vite Build-Time Injection

Vite bakes API URLs into the code during the `npm run build` phase. If you change the backend URL *after* building the container, it won't work. We solved this using **Build Args**.

### docker-compose.yml:
```yaml
frontend:
  build:
    context: ./frontend
    args:
      - VITE_API_URL=${VITE_API_URL} # Propagation from .env to Dockerfile
```

### Resulting Dockerfile logic:
```dockerfile
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build # VITE_API_URL is now "hard-coded" into the production JS
```

---

## 🗝️ 5. The `.env` Bridge

We centralized all configuration in a single `.env` file. This allows for rapid environment switching (Local vs. Prod) without modifying the `docker-compose.yml` or source code.

> [!TIP]
> **Security Best Practice**: Never commit the `.env` file to Version Control (Git). Always provide a `.env.example` with dummy values for other developers.

---

## 🚀 Summary Checklist
1. **Multi-stage Build**: Keep runtime images lean.
2. **Alpine Sync**: Always check for missing browser libraries in lightweight distros.
3. **Condition Checks**: Use `service_healthy` instead of just `depends_on`.
4. **Volume Persistence**: Ensure the DB data is mapped to a named volume (`postgres_data`) so it survives container restarts.
