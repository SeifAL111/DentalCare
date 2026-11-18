# Local setup — Séance 5

## Prerequisites

- Java 17
- Maven 3.9+

## Run

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
./scripts/start-s5.sh
```

Or manually: Eureka → business MS (H2) → API Gateway. See root `README.md`.

## Check

- http://localhost:8761 — Eureka dashboard (registered apps)
- http://localhost:8080/actuator/health — Gateway
- http://localhost:8081/swagger-ui/index.html — example Swagger
