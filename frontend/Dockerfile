# ---------- Etapa 1: compilar Angular ----------
FROM node:22-alpine AS build
WORKDIR /app

# Primero solo el manifiesto: npm ci queda en caché mientras no cambie.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# ---------- Etapa 2: servir con nginx ----------
FROM nginx:alpine
ENV BACKEND_URL=http://backend:8080
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 8080
