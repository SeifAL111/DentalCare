#!/usr/bin/env bash
# Séance 5 only: Eureka + H2 business services + API Gateway
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOGDIR="${TMPDIR:-/tmp}/dentalcare-s5"
mkdir -p "$LOGDIR"

export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"
export PATH="$JAVA_HOME/bin:$PATH"

wait_http() {
  local url="$1" name="$2" tries="${3:-45}"
  for ((i=1; i<=tries; i++)); do
    if curl -sf "$url" >/dev/null 2>&1; then
      echo "[ok] $name"
      return 0
    fi
    sleep 2
  done
  echo "[fail] $name ($url)"
  return 1
}

start_svc() {
  local s="$1" port="$2"
  if curl -sf "http://127.0.0.1:${port}/actuator/health" >/dev/null 2>&1; then
    echo "[skip] $s already up"
    return 0
  fi
  echo "[start] $s :$port"
  nohup mvn -f "$ROOT/Backend/$s/pom.xml" -q spring-boot:run >"$LOGDIR/$s.log" 2>&1 &
  echo $! >"$LOGDIR/$s.pid"
}

start_svc eureka-server 8761
wait_http "http://127.0.0.1:8761/actuator/health" "eureka"

# H2 services — enough to demonstrate Gateway + Eureka for S5
start_svc dossier-medical 8081
start_svc plan-traitement 8082
start_svc agenda-clinique 8083
wait_http "http://127.0.0.1:8081/actuator/health" "dossier-medical"

start_svc api-gateway 8080
wait_http "http://127.0.0.1:8080/actuator/health" "api-gateway"

echo
echo "Eureka:  http://localhost:8761"
echo "Gateway: http://localhost:8080/actuator/health"
echo "Swagger: http://localhost:8081/swagger-ui/index.html"
echo "Logs:    $LOGDIR"
