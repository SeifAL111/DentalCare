# laboratoire-protheses

- **Responsibility:** Prosthesis orders and manufacturing workflow
- **Owner:** Seifeddine Alaimi
- **Port:** 8085
- **Database:** PostgreSQL
- **Stack:** Spring Boot 4.1.2 · Spring Cloud 2025.1.4 · Java 17

## Run

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || echo /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home)
mvn spring-boot:run
```

## Swagger

http://localhost:8085/swagger-ui.html
