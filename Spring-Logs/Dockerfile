FROM openjdk:8
LABEL maintainer="17zwd.com"
WORKDIR /
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]