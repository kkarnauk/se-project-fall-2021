FROM openjdk:11-jre-slim AS build
WORKDIR /bookmarks
COPY . .
RUN ./gradlew build --no-daemon
