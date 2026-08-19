FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -B -DskipTests package && cp target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S confiapix && adduser -S confiapix -G confiapix
COPY --from=build /app/app.jar app.jar
COPY docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x docker-entrypoint.sh && chown confiapix:confiapix app.jar docker-entrypoint.sh
USER confiapix
EXPOSE 8080
ENTRYPOINT ["./docker-entrypoint.sh"]
