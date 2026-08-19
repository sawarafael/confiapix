#!/bin/sh
set -e

# Render/Heroku: postgres://user:pass@host:port/db -> JDBC
if [ -n "${DATABASE_URL:-}" ] && [ -z "${DB_URL:-}" ]; then
  url="$DATABASE_URL"
  case "$url" in
    postgresql://*) url="postgres://${url#postgresql://}" ;;
  esac
  rest="${url#postgres://}"
  userpass="${rest%%@*}"
  hostportdb="${rest#*@}"
  export DB_USERNAME="${DB_USERNAME:-${userpass%%:*}}"
  export DB_PASSWORD="${DB_PASSWORD:-${userpass#*:}}"
  case "$hostportdb" in
    *\?*) jdbc="jdbc:postgresql://${hostportdb}" ;;
    *)    jdbc="jdbc:postgresql://${hostportdb}?sslmode=require" ;;
  esac
  export DB_URL="$jdbc"
fi

PORT="${PORT:-${SERVER_PORT:-8080}}"

exec java ${JAVA_OPTS:--XX:MaxRAMPercentage=75.0 -XX:+UseG1GC} \
  -Djdk.httpclient.allowRestrictedHeaders=host \
  -jar app.jar \
  --server.port="$PORT"
