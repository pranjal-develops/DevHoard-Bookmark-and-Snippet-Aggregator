# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lightweight Runtime (Java 25)
# Note: Using Temurin 21 as it is the most stable LTS for now, 
# but 25 can be used if you have the specific image tag!
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install: Chromium, Driver, and Certs
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    ca-certificates \
    ttf-freefont \
    libstdc++ \
    nss \
    freetype \
    ttf-dejavu \
    udev
# Tell the system where Chromium lives
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_PATH=/usr/lib/chromium/

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
