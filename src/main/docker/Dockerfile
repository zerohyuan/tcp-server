FROM openjdk:8-jdk-alpine

VOLUME /tmp

ADD tcp-server-1.0.1-SNAPSHOT.jar tcp-server.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tcp-server.jar"]