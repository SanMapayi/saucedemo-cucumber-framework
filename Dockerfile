
FROM maven:3.9.9-eclipse-temurin-23

WORKDIR /app

# Cache Maven dependencies first
COPY pom.xml /app/pom.xml
RUN mvn -q -DskipTests dependency:go-offline

# Copy the rest of the project
COPY . /app

# Default command (docker-compose can override this)
CMD ["mvn", "-q", "clean", "test"]
