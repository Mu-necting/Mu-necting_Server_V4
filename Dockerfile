FROM openjdk:21
WORKDIR /munecting
COPY Munecting-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]