FROM eclipse-temurin:21-jdk

# Install Python and pip for price scraper
RUN apt-get update && \
    apt-get install -y python3 python3-pip python3-venv && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Gradle wrapper and build configuration from nestuity-service directory
COPY nestuity-service/build.gradle nestuity-service/settings.gradle nestuity-service/gradlew ./
COPY nestuity-service/gradle gradle
RUN chmod +x gradlew

# Download dependencies only once
RUN ./gradlew dependencies --no-daemon

# Copy price-scraper directory and install Python dependencies
COPY price-scraper /app/price-scraper
RUN pip3 install --no-cache-dir -r /app/price-scraper/requirements.txt --break-system-packages

# Start Spring Boot directly from source (no fat JAR)
CMD ["./gradlew", "bootRun", "--continuous", "--no-daemon"]
