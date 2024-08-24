FROM openjdk:17-jdk-slim-buster

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew --version && ./gradlew clean build --no-daemon --parallel --stacktrace || return 0

COPY src ./src

RUN ./gradlew build --no-daemon --stacktrace

ADD build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
