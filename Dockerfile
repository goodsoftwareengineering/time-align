FROM openjdk:8-alpine

COPY target/uberjar/time-align.jar /time-align/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/time-align/app.jar"]
