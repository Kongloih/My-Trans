FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app
COPY target/transaction-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
