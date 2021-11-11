FROM gradle:7.2-jdk11-alpine AS build
WORKDIR /home/gradle/src
RUN git clone -b task_4 https://github.com/kkarnauk/se-project-fall-2021.git .
RUN gradle build --no-daemon

RUN rm build/libs/*-plain.jar
FROM openjdk:11-jre-slim
COPY --from=build /home/gradle/src/build/libs/*.jar application/app.jar
RUN ls application
ENTRYPOINT ["java", "-jar", "application/app.jar"]
