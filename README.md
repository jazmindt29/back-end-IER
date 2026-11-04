# Plataforma IER — Instituto de Energías Renovables

Angular 20 + Spring Boot 4.0.1 (Java 17+) + PostgreSQL 16.
Frontend: http://localhost:4200. API: http://localhost:8080/api.
Durante `npm start`, Angular redirige `/api` al backend local mediante
`frontend/proxy.conf.json`; el navegador ya no tiene una URL de API fija.

## Bases de desarrollo independientes (Linux y Windows)

El código y `backend/src/main/resources/db/migration` se comparten por Git;
**los datos de PostgreSQL no se sincronizan**. Cada PC usa su propia base nueva,
por ejemplo `ier_dev_v2`. Las bases `IER` antiguas se conservan sin modificar.
Flyway aplica las migraciones pendientes en esa base al arrancar el backend;
Hibernate solo valida el resultado (`ddl-auto=validate`). No usar esta primera
migración en una base `IER` que ya tenga tablas.

En Linux, con el contenedor `ier-postgres` existente, crear la base nueva solo
una vez (el nombre debe estar libre):

```bash
docker exec ier-postgres psql -U postgres -d postgres -c 'CREATE DATABASE ier_dev_v2;'
```

En PowerShell de Windows, después de hacer `git pull` y revisar los cambios
locales, el equivalente es:

```powershell
docker exec ier-postgres psql -U postgres -d postgres -c "CREATE DATABASE ier_dev_v2;"
```

Estos comandos **crean** una base vacía; no borran tablas, contenedores ni
volúmenes. Si ya existe la base, no volver a ejecutar `CREATE DATABASE`.
Configurar en **cada PC** `IER_DB_URL=jdbc:postgresql://localhost:5432/ier_dev_v2`,
`IER_DB_USERNAME`, `IER_DB_PASSWORD` y un `JWT_SECRET` propio de al menos 32 bytes.
Para no exportar variables en cada terminal, copiar una sola vez
`backend/.env.example` como `backend/.env.local` y editar ahí las credenciales
locales. Ese archivo está excluido de Git; cada PC conserva sus propios valores.
No enviar ni versionar `.env.local`.

En Windows, crear el archivo una vez en PowerShell:

```powershell
cd backend
Copy-Item .env.example .env.local
```

Editar `.env.local` con la contraseña del PostgreSQL de esa PC. Si la base está
vacía, agregar también `APP_BOOTSTRAP_ADMIN_USERNAME` y
`APP_BOOTSTRAP_ADMIN_PASSWORD` (mínimo 12 caracteres) para crear su administrador
inicial. Luego arrancar con `./run.ps1` desde PowerShell en `backend`; requiere JDK
17+ configurado. En Linux/macOS, el equivalente es copiar `.env.example` a
`.env.local` y ejecutar `./run.sh`. Ambos scripts cargan el archivo sin exportar
variables manualmente. No ejecutar el arranque contra la antigua `IER`.

## Arranque local (Linux/macOS)

Se requieren Docker, un JDK compatible (verificado con JDK 21), Node y npm.
El lockfile de Angular admite Node ^20.19, ^22.12 o >=24.

1. Iniciar el contenedor existente, conservando su volumen, y crear la base nueva
   según las instrucciones anteriores:

   ```bash
   docker start ier-postgres
   ```

   Configurar `backend/.env.local` para la base nueva. No recrear el contenedor para
   solucionar problemas de autenticación.

2. En una terminal, desde la raíz del repositorio:

   ```bash
   cd backend
   ./run.sh
   ```

   El script activa `dev` si no se indicó otro perfil y neutraliza las variables globales
   `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` de otros proyectos.

3. En otra terminal, desde la raíz:

   ```bash
   cd frontend
   npm ci
   npm start
   ```

   `npm ci` instala exactamente el lockfile y reemplaza node_modules; no hace falta repetirlo en cada arranque.

4. Abrir http://localhost:4200. Cada servidor ocupa su terminal hasta detenerlo con Ctrl+C.

El proxy de desarrollo solo sirve a `ng serve`. En un despliegue, publicar el
frontend y enrutar `/api` hacia Spring Boot desde el mismo origen (idealmente HTTPS).
No publicar el servidor de desarrollo como sustituto de un despliegue.

## Cuenta inicial y sesiones

Las cuentas existentes se conservan. No se crea ningún administrador con contraseña fija.
Solo en perfil `dev` o `bootstrap`, si la tabla de usuarios está vacía, se puede configurar:

- `APP_BOOTSTRAP_ADMIN_USERNAME`
- `APP_BOOTSTRAP_ADMIN_PASSWORD` (mínimo 12 caracteres)

Los roles se buscan por nombre, sin depender del ID 4.
En desarrollo, si no se configura `JWT_SECRET`, se genera una clave aleatoria por arranque.
Después de reiniciar el backend hay que iniciar sesión nuevamente. Un JWT de una cuenta
eliminada, deshabilitada o con rol cambiado ya no autoriza peticiones.

## Correo e invitaciones

Si se configura SMTP, agregar `SPRING_MAIL_USERNAME` y `SPRING_MAIL_PASSWORD` a
`backend/.env.local`, usando una contraseña de aplicación nueva. La contraseña SMTP
anteriormente versionada debe revocarse en la cuenta de correo.

- Sin SMTP, la invitación se guarda y la UI informa que el correo no se envió.
- Una vez corregido SMTP, usar **Reenviar invitación** con nombre y correo.
- Reenviar reemplaza el token anterior y concede otras 48 horas.
- El enlace solo se puede activar una vez.
- `APP_FRONTEND_URL` debe ser la URL accesible para el destinatario; localhost solo sirve en la misma máquina.
- Sin `APP_FRONTEND_URL` válida, el backend guarda la invitación pero **no envía** un enlace roto.
- `APP_MAIL_CONTACTO` es el destinatario de notificaciones de contacto.
- No se imprimen enlaces de activación ni cuerpos de correo en los logs.

El ejemplo está en [backend/.env.example](backend/.env.example). No guardar secretos
en archivos versionados; solo `run.sh` y `run.ps1` cargan `.env.local`.

## Producción

El perfil común exige `IER_DB_URL`, `IER_DB_USERNAME`, `IER_DB_PASSWORD` y `JWT_SECRET`
(clave propia de al menos 32 bytes). No activar `dev` en producción.

La configuración común y desarrollo validan el esquema (`ddl-auto=validate`).
Flyway aplica únicamente las migraciones versionadas. V2 añade índices de consultas
y unicidad de tokens; la migración inicial está
dirigida a una base **vacía**; no se habilitó `baseline-on-migrate` para evitar
adoptar silenciosamente una base antigua. Antes de migrar datos reales se requiere
un plan específico y un respaldo.

Configurar `APP_CORS_ORIGINS` y `APP_FRONTEND_URL` para el dominio real. El frontend
usa `/api` en el mismo origen; el proxy o servidor web debe dirigirlo al backend.
No apuntar el correo a `localhost` si los destinatarios usarán otro dispositivo.

La CI de GitHub en `.github/workflows/ci.yml` prueba el backend sobre PostgreSQL
temporal, compila el frontend, ejecuta Karma y audita dependencias de ejecución.
No usa las bases `IER` de desarrollo.

## Verificación

Pruebas backend unitarias, sin PostgreSQL ni SMTP:

```bash
cd backend
JAVA_HOME=/opt/android-studio/jbr ./mvnw test
```

Frontend:

```bash
cd frontend
npm run build
CHROME_BIN=/usr/bin/google-chrome-stable npm test -- --watch=false --browsers=ChromeHeadless
```

Las pruebas de integración están deshabilitadas por defecto y solo admiten una base
de pruebas separada en 127.0.0.1:55432/ier_test. Instrucciones, alcance, resultados
y tareas pendientes: [docs/PLAN_CORRECCIONES.md](docs/PLAN_CORRECCIONES.md).
