FROM java:8

ADD target/vulnerable-api.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=container","-jar","/app.jar"]