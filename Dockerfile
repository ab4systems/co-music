FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/comusic.jar /comusic/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/comusic/app.jar"]
