# syntax=docker/dockerfile:1
# Imagen única para Render: compila Angular y Spring Boot y ejecuta ambos
# procesos detrás de nginx. Render usa este Dockerfile de la raíz por defecto.

# ---------- Etapa 1: frontend (Angular) ----------
FROM node:22-alpine AS frontend
WORKDIR /build
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build -- --configuration production

# ---------- Etapa 2: backend (Spring Boot) ----------
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /build
COPY backend/pom.xml .
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
RUN mvn -B -q -DskipTests package \
    && cp target/backend-*.jar app.jar

# ---------- Etapa 3: ejecución ----------
FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends nginx supervisor gettext-base \
    && rm -rf /var/lib/apt/lists/* \
    && rm -f /etc/nginx/sites-enabled/default \
    && groupadd --system app \
    && useradd --system --gid app --no-create-home app

WORKDIR /app
COPY --from=backend /build/app.jar /app/app.jar
COPY --from=frontend /build/dist/frontend/browser /usr/share/nginx/html
COPY frontend/nginx.conf /app/nginx.conf.template
COPY deploy/supervisord.conf /etc/supervisor/conf.d/ier.conf
COPY deploy/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# nginx queda público en 8080 (Render usa PORT=8080) y Spring Boot atiende
# solo en localhost:8081.
ENV BACKEND_URL="http://127.0.0.1:8081" \
    SERVER_PORT="8081" \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
