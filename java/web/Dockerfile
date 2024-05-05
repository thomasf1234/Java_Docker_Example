FROM openjdk:14-jdk-alpine
COPY out/artifacts/web_jar/web.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]