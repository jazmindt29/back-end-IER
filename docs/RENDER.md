# Despliegue en Render (un solo servicio)

Render no soporta `docker-compose.yml`. Para evitar configurar dos servicios,
contextos y redes, este repositorio incluye un `Dockerfile` en la raíz que:

1. Compila Angular (`frontend/`).
2. Compila Spring Boot (`backend/`).
3. Ejecuta nginx y el backend en el mismo contenedor, con supervisord.

nginx atiende el puerto público `8080` y reenvía `/api` a Spring Boot en
`127.0.0.1:8081`. La base de datos es un PostgreSQL gestionado por Render.

## Despliegue

1. Subir los cambios a GitHub.
2. En Render: **New → Blueprint**.
3. Seleccionar el repositorio. Render lee `render.yaml` y muestra:
   - el servicio web `ier`;
   - la base `ier-db`;
   - la variable `APP_BOOTSTRAP_ADMIN_PASSWORD`, que debes escribir
     (mínimo 12 caracteres);
   - las variables de correo (`BREVO_API_KEY`, `APP_MAIL_FROM`,
     `APP_MAIL_CONTACTO`), que se explican más abajo.
4. Aplicar el Blueprint. Render construye la imagen y despliega.
5. Abrir la URL que muestra Render e iniciar sesión con el usuario `admin` y la
   contraseña elegida.

En el primer arranque, Flyway crea el esquema y el perfil `bootstrap` crea el
administrador. Cuando ya exista, puedes cambiar `SPRING_PROFILES_ACTIVE` a
`prod` en el panel de Render; el sembrador no vuelve a crear cuentas mientras
haya usuarios en la base.

## Variables relevantes

| Variable | Valor |
|---|---|
| `PORT` | `8080` (puerto público de nginx) |
| `SERVER_PORT` | `8081` (Spring Boot interno) |
| `DATABASE_URL` | La inyecta Render desde `ier-db` |
| `IER_DB_USERNAME` / `IER_DB_PASSWORD` | Las inyecta Render desde `ier-db` |
| `JWT_SECRET` | Generado por Render |
| `APP_FRONTEND_URL` / `APP_CORS_ORIGINS` | URL pública del servicio |
| `BREVO_API_KEY` | API key de Brevo para enviar correos por HTTPS |
| `APP_MAIL_FROM` | Correo remitente verificado en Brevo |
| `APP_MAIL_FROM_NAME` | Nombre visible del remitente (`IER`) |
| `APP_MAIL_CONTACTO` | Buzón que recibe el formulario de contacto (opcional) |

`deploy/entrypoint.sh` convierte `DATABASE_URL` (`postgresql://...`) al formato
JDBC que espera Spring Boot.

## Correo de invitaciones con Brevo

Render **bloquea los puertos SMTP `25`, `465` y `587` en el plan gratuito**, por
eso Gmail SMTP no funciona ahí. La aplicación envía por la API HTTPS de Brevo
(puerto 443), que sí está permitida. SMTP queda como respaldo para desarrollo
local o para un plan pago de Render.

### Configurar Brevo

1. Crear una cuenta gratuita en [brevo.com](https://www.brevo.com/).
2. Verificar el remitente en **Senders, Domains & IPs → Senders → Add a sender**.
   Brevo envía un correo de confirmación; hay que abrirlo y confirmar.
3. Crear una API key en **SMTP & API → API keys → Create a new API key**.
   Copiarla en el momento, porque no se vuelve a mostrar.
4. En Render, abrir el servicio → **Environment** y agregar:

   ```
   BREVO_API_KEY=tu-api-key
   APP_MAIL_FROM=el-correo-que-verificaste@ejemplo.com
   APP_MAIL_FROM_NAME=IER
   APP_MAIL_CONTACTO=correo-del-departamento@ejemplo.com
   ```

   `APP_MAIL_CONTACTO` es opcional; si se omite, los mensajes del formulario se
   guardan pero no se notifican. `APP_MAIL_FROM_NAME` ya viene con `IER` en
   `render.yaml`, así que normalmente no hay que cambiarla.

5. Guardar los cambios. Render redepliega solo. Si se actualizó `render.yaml`,
   usar **Blueprint → Sync** para que aparezcan las variables nuevas.
6. Probar una invitación desde el panel. Si falla, revisar los logs de Render:
   `Brevo rechazó el correo (HTTP 401)` indica API key incorrecta; `HTTP 400`
   suele indicar que el remitente no está verificado.

El plan gratuito de Brevo permite 300 correos al día. Para producción conviene
autenticar el dominio en Brevo (SPF/DKIM) y usar un remitente del dominio de la
institución para mejorar la entregabilidad.

## Solución de problemas: 403 al iniciar sesión

Si al entrar con usuario y contraseña aparece **“No tienes permisos para realizar
esta operación”**, normalmente es un `403 Invalid CORS request`, no un problema
de credenciales.

Render termina TLS en su balanceador y entrega la petición a nginx por HTTP. Sin
las cabeceras `X-Forwarded-*`, Spring ve `http://...` mientras el navegador envía
un `Origin: https://...`; por eso considera el login como otro origen y lo
rechaza. La configuración incluida ya lo resuelve:

- `frontend/nginx.conf` conserva `X-Forwarded-Proto: https` y
  `X-Forwarded-Host`.
- `application.properties` activa `server.forward-headers-strategy=framework`.
- Si `APP_CORS_ORIGINS` llega vacío, el backend usa `RENDER_EXTERNAL_URL`.

Para comprobarlo en los logs de Render, busca `Invalid CORS request`. Si usas un
dominio propio, define el origen exacto (sin barra final) y sepáralo con comas si
hay varios:

```
APP_FRONTEND_URL=https://tudominio.com
APP_CORS_ORIGINS=https://tudominio.com,https://ier.onrender.com
```

## Limitaciones del plan gratuito

- El servicio se suspende tras unos minutos de inactividad y tarda en despertar.
- La base PostgreSQL gratuita de Render tiene límite de almacenamiento y caduca
  a los 30 días. Para producción, usar planes pagados.
- Los servicios gratuitos no pueden enviar tráfico saliente a los puertos SMTP
  `25`, `465` ni `587`. Por eso el correo usa Brevo por HTTPS; si se pasa a un
  plan pago, se puede volver a configurar SMTP con Gmail.
- Un contenedor con dos procesos no escala frontend y backend por separado.
  Si más adelante necesitas escalar, se pueden dividir en dos servicios.

## Probar la imagen en local

```bash
docker build -t ier-render .
```

La imagen espera `DATABASE_URL`, `IER_DB_USERNAME` e `IER_DB_PASSWORD`. Para
probar con un PostgreSQL temporal:

```bash
docker network create ier-test
docker run -d --rm --name ier-test-db --network ier-test \
  -e POSTGRES_DB=ier -e POSTGRES_USER=ier -e POSTGRES_PASSWORD=test \
  postgres:16

docker run --rm --name ier-test-app --network ier-test -p 8080:8080 \
  -e DATABASE_URL=postgresql://ier:test@ier-test-db:5432/ier \
  -e IER_DB_USERNAME=ier -e IER_DB_PASSWORD=test \
  -e JWT_SECRET=0123456789012345678901234567890123456789 \
  -e SPRING_PROFILES_ACTIVE=bootstrap \
  -e APP_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e APP_BOOTSTRAP_ADMIN_PASSWORD=AdminLocal12345 \
  -e APP_FRONTEND_URL=http://localhost:8080 \
  -e APP_CORS_ORIGINS=http://localhost:8080 \
  ier-render
```
