# Docker: stack completo del IER

Levanta PostgreSQL, el backend (Spring Boot) y el frontend (Angular servido por nginx)
con un solo comando. Usa una **base nueva** con su propio volumen (`ier_pgdata`):
no toca el contenedor `ier-postgres` ni las bases `IER` antiguas.

## Archivos

| Archivo | Para qué sirve |
|---|---|
| `docker-compose.yml` (raíz) | Define los 3 servicios, red y volumen |
| `.env.example` (raíz) | Plantilla de configuración; se copia a `.env` |
| `backend/Dockerfile` | Compila con Maven (JDK 21) y ejecuta con JRE 21 |
| `frontend/Dockerfile` | Compila Angular (Node 22) y sirve con nginx |
| `frontend/nginx.conf` | Sirve la SPA y reenvía `/api` al backend |
| `*/.dockerignore` | Mantiene fuera de la imagen `node_modules`, `target`, `.env`, etc. |

## Primer arranque

```bash
cp .env.example .env
```

Editar `.env`: como mínimo `POSTGRES_PASSWORD` y `JWT_SECRET`
(`openssl rand -base64 48` genera una clave válida). Si la base está vacía y quieres un
administrador inicial, descomentar `IER_PROFILE=bootstrap` y las variables
`APP_BOOTSTRAP_ADMIN_*` (contraseña de 12+ caracteres).

```bash
docker compose up -d --build
docker compose logs -f backend      # Ctrl+C para salir; esperar "Started ApiApplication"
```

Abrir http://localhost:8080 (o el `WEB_PORT` configurado). Flyway crea el esquema
al arrancar el backend. Cuando el administrador ya exista, vuelve a comentar
`IER_PROFILE` y las credenciales de bootstrap y ejecuta `docker compose up -d`.

## Uso diario

```bash
docker compose up -d                  # iniciar
docker compose stop                   # detener (conserva los datos)
docker compose ps                     # estado y salud de los servicios
docker compose logs -f backend        # logs
docker compose up -d --build          # tras hacer git pull con cambios de código
```

## Solo PostgreSQL (desarrollo con ./run.sh)

Si prefieres seguir ejecutando backend y frontend con `./run.sh` y `npm start`, pero
quieres la base en Docker:

```bash
docker compose up -d db
```

y en `backend/.env.local`:

```
IER_DB_URL=jdbc:postgresql://localhost:5433/ier
IER_DB_USERNAME=ier
IER_DB_PASSWORD='la misma de POSTGRES_PASSWORD'
```

## Respaldo y restauración

```bash
docker compose exec -T db pg_dump -U ier ier > respaldo.sql
docker compose exec -T db psql -U ier -d ier < respaldo.sql   # restaurar en una base vacía
```

## Cuidado

- `docker compose down` conserva los datos; **`docker compose down -v` borra el volumen
  y con él toda la base**.
- Cambiar `POSTGRES_PASSWORD` en `.env` después del primer arranque **no** cambia la
  contraseña ya guardada en el volumen.
- Si cambias `JWT_SECRET`, todas las sesiones activas piden iniciar sesión de nuevo.
- Para publicarlo en internet, poner delante un proxy con HTTPS (Caddy, Traefik o el
  nginx del servidor) y definir `APP_FRONTEND_URL` y `APP_CORS_ORIGINS` con el dominio real.
- Agregar `.env` al `.gitignore` si todavía no está.

## Problemas comunes

| Síntoma | Causa probable |
|---|---|
| `required variable POSTGRES_PASSWORD is missing` | Falta crear `.env` o completar la variable |
| El backend se reinicia en bucle | Ver `docker compose logs backend`; suele ser `JWT_SECRET` de menos de 32 bytes |
| `port is already allocated` | Cambiar `WEB_PORT` o `DB_PORT` en `.env` |
| Error 403 "Invalid CORS request" | `APP_CORS_ORIGINS` no coincide con la URL con la que abres el sitio |
| Las invitaciones no envían correo | Configurar SMTP en `.env` y usar **Reenviar invitación** |
