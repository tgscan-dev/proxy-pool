FROM maven:3.9.8-sapmachine-22 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:22-slim
EXPOSE 8080

COPY --from=build /app/target/proxy-pool-0.1.0.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
