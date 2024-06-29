FROM openjdk:17-jdk-slim-buster

# Set the working directory
WORKDIR /app

# Copy the build.gradle and gradlew files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies
RUN ./gradlew build --no-daemon || return 0

# Copy the rest of the application source code
COPY src ./src

# Build the application
RUN ./gradlew build --no-daemon

# Add the generated jar file to the image
ADD build/libs/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
