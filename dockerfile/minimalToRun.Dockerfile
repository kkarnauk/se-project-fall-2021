FROM openjdk:11-jre-slim AS build
WORKDIR /bookmarks
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM openjdk:11-jre-slim
COPY --from=build /bookmarks/build/libs/*.jar bookmarks/app.jar
ENTRYPOINT ["java", "-jar", "bookmarks/app.jar"]
