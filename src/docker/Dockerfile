FROM openjdk:13-alpine3.9

ADD *.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dfile.encoding=utf-8","-jar","/app.jar","--spring.profiles.active=dev"]