# Plataforma IER — Instituto de Energías Renovables

Sistema web del instituto: catálogo público de laboratorios, investigadores, proyectos y avisos; calendario de reservas de laboratorios; y paneles privados de gestión para administradores e investigadores.

| | Tecnología | Puerto |
|---|---|---|
| `backend/` | Spring Boot 4 (Java 17) + PostgreSQL, JWT + BCrypt | 8080 |
| `frontend/` | Angular 20 (standalone) + Bootstrap 5 | 4200 |

## Cómo ejecutarlo

```bash
./start.sh                                        # Linux / macOS / Git Bash / WSL
powershell -ExecutionPolicy Bypass -File start.ps1   # Windows (PowerShell o cmd)
```

Eso es todo: el script levanta la base de datos en Docker (crea el contenedor si no existe), arranca el backend y espera a que responda, y abre el frontend. `Ctrl+C` detiene backend y frontend (el contenedor de BD queda corriendo). Log del backend: `/tmp/ier-backend.log` (Windows: `%TEMP%\ier-backend.log`).

Abrir <http://localhost:4200>. Usuario administrador de desarrollo: `admin` / `admin123`.

<details>
<summary>Levantar cada parte por separado</summary>

```bash
# 1. Base de datos
docker start ier-postgres || docker run --name ier-postgres \
  -e POSTGRES_PASSWORD=jazmin -e POSTGRES_DB=IER -p 5432:5432 -d postgres:16

# 2. Backend
cd backend && ./run.sh

# 3. Frontend (en otra terminal)
cd frontend && npm install && npm start
```
</details>

En el primer arranque Hibernate crea las tablas nuevas y las contraseñas en texto plano se cifran automáticamente con BCrypt. La tabla `roles` debe tener `ADMIN` (o `ADMINISTRADOR`) e `INVESTIGADOR` con id 4.

## Configuración (variables de entorno)

Todas opcionales en desarrollo (hay defaults apuntando al contenedor Docker):

- `IER_DB_URL`, `IER_DB_USERNAME`, `IER_DB_PASSWORD` — conexión a PostgreSQL
- `JWT_SECRET` — clave de firma de los tokens (obligatoria en producción, mín. 32 bytes)
- `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` — SMTP para invitaciones y contacto (sin ellas, los enlaces de invitación quedan en el log del backend)
- `APP_FRONTEND_URL`, `APP_CORS_ORIGINS`, `APP_MAIL_CONTACTO`

## Documentación

La guía técnica completa (arquitectura, endpoints, matriz de roles, modelo de datos, flujos) está en [`PROJECT_CONTEXT.md`](PROJECT_CONTEXT.md).
