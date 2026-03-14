FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/erp-clinique.war /app/erp-clinique.war

EXPOSE 8081

ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/clinique_erp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=mysql

ENTRYPOINT ["java","-jar","/app/erp-clinique.war"]

