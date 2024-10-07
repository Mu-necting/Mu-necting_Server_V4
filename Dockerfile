FROM openjdk:21
WORKDIR /munecting
COPY munecting-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]