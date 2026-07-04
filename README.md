# Plataforma IER — Instituto de Energías Renovables

Sistema web del instituto: catálogo público de laboratorios, investigadores, proyectos y avisos; calendario de reservas de laboratorios; y paneles privados de gestión para administradores e investigadores.

| | Tecnología | Puerto |
|---|---|---|
| `backend/` | Spring Boot 4 (Java 17) + PostgreSQL, JWT + BCrypt | 8080 |
| `frontend/` | Angular 20 (standalone) + Bootstrap 5 | 4200 |

## Cómo ejecutarlo

```bash
# 1. Base de datos (contenedor de desarrollo)
docker run --name ier-postgres -e POSTGRES_PASSWORD=jazmin -e POSTGRES_DB=IER \
  -p 5432:5432 -d postgres:16
# (si ya existe: docker start ier-postgres)

# 2. Backend
cd backend && ./run.sh

# 3. Frontend (en otra terminal)
cd frontend && npm install && npm start
```

Abrir <http://localhost:4200>. Usuario administrador de desarrollo: `admin` / `admin123`.

En el primer arranque Hibernate crea las tablas nuevas y las contraseñas en texto plano se cifran automáticamente con BCrypt. La tabla `roles` debe tener `ADMIN` (o `ADMINISTRADOR`) e `INVESTIGADOR` con id 4.

## Configuración (variables de entorno)

Todas opcionales en desarrollo (hay defaults apuntando al contenedor Docker):

- `IER_DB_URL`, `IER_DB_USERNAME`, `IER_DB_PASSWORD` — conexión a PostgreSQL
- `JWT_SECRET` — clave de firma de los tokens (obligatoria en producción, mín. 32 bytes)
- `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` — SMTP para invitaciones y contacto (sin ellas, los enlaces de invitación quedan en el log del backend)
- `APP_FRONTEND_URL`, `APP_CORS_ORIGINS`, `APP_MAIL_CONTACTO`

## Documentación

La guía técnica completa (arquitectura, endpoints, matriz de roles, modelo de datos, flujos) está en [`PROJECT_CONTEXT.md`](PROJECT_CONTEXT.md).
