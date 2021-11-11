FROM gradle:7.2-jdk11-alpine AS build
WORKDIR /home/gradle/src
RUN git clone -b task_4 https://github.com/kkarnauk/se-project-fall-2021.git .
RUN gradle build --no-daemon
