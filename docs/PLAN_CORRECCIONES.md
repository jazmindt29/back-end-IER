# Registro de correcciones

Inicio: 2026-11-03. La revisión inicial fue de solo lectura. La implementación fue autorizada después.

## Alcance y orden

1. Configuración y secretos: separar desarrollo, exigir JWT seguro y desactivar altas automáticas fuera de desarrollo.
2. Creaciones seguras: DTO de entrada y rechazo de IDs para evitar actualizaciones no autorizadas.
3. Invitaciones: bloqueo de doble envío, errores diferenciados, reenvío y correo después del commit.
4. Integridad: reservas concurrentes, borrados con dependencias y eliminación de encargado.
5. Transacciones, excepciones y auditoría.
6. Frontend: cargas, errores, respuestas fuera de orden e imagen de fondo.
7. Pruebas de regresión y documentación de ejecución.

## Reglas de trabajo

- Preservar cambios existentes en cachés y artefactos versionados.
- No borrar volúmenes ni usar la base IER para pruebas que escriban datos.
- No enviar correos reales durante las pruebas.
- Validar cada tanda antes de darla por terminada.
- No incluir secretos ni enlaces de activación en este documento o en logs.

## Seguimiento

La implementación principal y la tanda de bases nuevas con Flyway se verificaron
el 2026-11-03. Los pendientes de diseño y operación se enumeran por separado.

### Tanda 1 — Configuración y sesiones

- Se retiraron las credenciales SMTP literales y las propiedades de contacto duplicadas.
- `application.properties` exige configuración externa y valida el esquema; `application-dev.properties` conserva el arranque local explícito.
- `run.sh` activa dev si no se indicó otro perfil. Sin JWT_SECRET, dev usa una clave aleatoria por arranque.
- Se rechazan claves JWT cortas y el antiguo secreto predeterminado.
- Seeder y migración BCrypt solo se ejecutan en dev. El administrador inicial requiere credenciales externas; se eliminaron las credenciales fijas.
- Roles por nombre; no se depende del ID 4.
- La validación JWT comprueba issuer, cuenta habilitada y rol actual. El frontend descarta sesiones vencidas y revocadas.
- Se eliminó CorsConfig duplicado; SecurityConfig concentra CORS.

### Tanda 2 — Entradas y datos públicos

- DTO validados para contacto, reserva y alta administrativa; los campos ID se rechazan.
- Los demás POST de creación rechazan IDs explícitos. Se usan referencias persistidas para laboratorio/investigador de reservas.
- El servicio asigna al investigador autenticado; un cliente no puede reservar a nombre de otro investigador.
- Las cuentas vinculadas a perfiles y los usuarios autores de avisos se excluyen del JSON público.
- Contacto admite texto largo y limita longitudes de entrada.

### Tanda 3 — Invitaciones

- El botón muestra Enviando, se bloquea y el método evita doble solicitud. También se protegen los formularios CRUD administrativos, contacto y login.
- El feedback de éxito no se sustituye por una segunda solicitud del mismo clic.
- Reenvío explícito para cuentas pendientes: conserva la cuenta, reemplaza el token y reinicia las 48 horas.
- La respuesta distingue enviado=true y guardado sin envío SMTP. Se conserva el formulario para reintentar mediante Reenviar.
- El correo se solicita después del commit, con timeouts de cinco segundos y sin registrar tokens, cuerpos ni destinatarios.
- El interceptor excluye solo las rutas públicas de autenticación y nunca adjunta JWT a otro servidor. Conserva JWT para invitar/reenviar.
- Activación distingue error de conexión/sesión/servidor de un rechazo real de invitación. Permite reintentar errores operativos.
- Activar bloquea la cuenta y consume el token en la misma transacción.
- Se retiraron las implementaciones duplicadas bajo /api/usuarios; el frontend ya usaba /api/auth.

### Tanda 4 — Integridad y transacciones

- Reservar bloquea el laboratorio con PESSIMISTIC_WRITE antes de leer horarios: dos solicitudes concurrentes no pueden crear el mismo rango desde esta API.
- Los borrados verifican referencias y responden 409; no se agregaron cascadas destructivas para resolverlas.
- Se retiró la cascada ALL/orphanRemoval de Investigador hacia Usuario. El borrado explícito mantiene el control de ambas entidades.
- Seleccionar Sin encargado persiste null.
- Cambios de proyecto, laboratorio e investigador comparten transacción con su auditoría.
- Conflictos de integridad y permisos tienen mensajes controlados. No se convierten fallos arbitrarios de base de datos en errores de token.
- Se usa inyección por constructor en los servicios principales modificados.

### Tanda 5 — Frontend, pruebas e higiene

- Respuestas antiguas de filtros y materiales se cancelan; las cargas muestran espera y error.
- Admin descarga datos por pestaña y dependencias, en vez de recargar seis colecciones después de cada acción.
- La imagen src/assets/pic.jpg se incluye en la salida de producción.
- Inicio, calendario y panel de investigador comunican errores de carga; perfil y reserva bloquean envíos repetidos. El calendario no presenta un fallo de consulta como confirmación de disponibilidad.
- Se repararon imports/dependencias de tests anteriores y se añadieron regresiones de los flujos corregidos.
- Se retiraron del índice Git 95 archivos bajo backend/target y frontend/.angular. Las cachés se conservaron; target se recompiló desde cero para excluir clases de fuentes eliminadas. Los artefactos son regenerables.
- README y PROJECT_CONTEXT describen la configuración y los contratos actuales.

### Tanda 6 — Bases independientes reproducibles

- Se añadió Flyway para PostgreSQL y V1 con las tablas, claves, roles y texto largo de contacto.
- `dev` ahora valida el esquema; no crea ni altera tablas mediante Hibernate.
- `bootstrap` permite crear el administrador inicial con variables externas en una base vacía.
- La primera migración solo es para bases nuevas; no se habilitó baseline sobre `IER` existente.
- La integración aislada comienza con un esquema vacío, ejecuta Flyway y valida JPA.
- Se corrigieron login con mayúsculas en correo, falsos horarios libres tras error y respuestas
  tardías que ocultaban reservas o descartaban un segundo borrador administrativo.
- La interfaz muestra credenciales incorrectas para un 401 de login, no sesión vencida.

### Tanda 7 — Seguridad, contratos y arquitectura incremental

- Angular se actualizó a 20.3.35 en paquetes de ejecución y compilación; `npm ci`
  instala el lockfile sin opciones especiales. `npm audit --omit=dev` quedó en cero
  avisos. La auditoría completa conserva 22 avisos moderados de herramientas de
  desarrollo; no se forzó un downgrade mayor de Angular/Karma para ocultarlos.
- El frontend usa `/api` bajo el mismo origen. `ng serve` aplica `proxy.conf.json`.
  En despliegue se requiere proxy HTTP y `APP_FRONTEND_URL` accesible; fuera de dev,
  una URL ausente o inválida impide enviar un enlace de invitación roto.
- `GET /api/reservas` y la respuesta de creación usan un DTO mínimo: no se envían
  correo, biografía ni entidad completa del investigador. Se rechazan reservas de
  horas ya iniciadas hoy.
- Proyectos usa DTO de entrada, valida IDs de investigadores y establece createdBy
  desde la sesión administrativa. Las entidades JPA usan getters/setters sin el
  `toString`, `equals` y `hashCode` generados por `@Data` sobre campos sensibles.
- Flyway V2 añade índices para disponibilidad, relaciones y búsqueda única del token.
  La migración se probó desde una base temporal vacía junto con V1.
- Las rutas Angular se cargan bajo demanda. El menú móvil usa estado Angular; se
  retiró el bundle JavaScript de Bootstrap que solo se usaba para abrirlo. Las
  invitaciones se extrajeron a un componente autónomo del panel administrativo.
- Se añadió CI de GitHub con PostgreSQL 16 aislado, backend, frontend, Karma y
  auditoría de dependencias de ejecución. Su YAML se validó localmente; la
  ejecución real en GitHub queda pendiente hasta publicar los cambios.
- Había 6 763 archivos de `frontend/node_modules` versionados pese a estar en
  `.gitignore`. Se retiraron **solo del índice Git**; permanecen en disco y se
  regeneran con `npm ci`. El lockfile sí queda versionado.

### Tanda 8 — Arranque local sin exportaciones manuales

- `backend/run.sh` y el nuevo `backend/run.ps1` leen `backend/.env.local` antes de
  iniciar Spring Boot; el ejemplo versionado indica cómo crear ese archivo una sola vez.
- `.env.local` de la computadora actual quedó configurado para su PostgreSQL local y
  está excluido de Git. No contiene credenciales SMTP; la contraseña anterior se
  considera comprometida y debe reemplazarse por una contraseña de aplicación nueva.
- README actualizado con el mismo flujo para Linux/macOS y PowerShell en Windows.
- Verificación local de sintaxis Bash, exclusión Git, diff hygiene y revisión estática
  del script PowerShell. La ejecución real de `run.ps1` debe confirmarse en Windows.

### Tanda 9 — Catálogo público y usabilidad

- Se añadió `/proyectos` público, con carga diferida, pestaña de navegación y enlace
  desde Inicio. Consulta todos los proyectos, no solo los destacados; incluye búsqueda
  por título, descripción, estado e investigador, estados vacíos y reintento de errores.
- Inicio sigue mostrando exclusivamente los destacados. El catálogo no requiere
  cambios en el backend ni migraciones de base de datos.
- Contacto admite correos válidos de cualquier dominio, igual que su DTO backend;
  se quitaron la restricción Gmail y la etiqueta institucional incorrecta. Los campos
  respetan las longitudes máximas admitidas por el servidor.
- Fechas configuradas en español de México. El calendario usa botones enfocables
  para seleccionar días y etiquetas accesibles para avanzar/retroceder períodos.
- Se corrigió desbordamiento de la navegación con administrador y correo largo:
  menú colapsado antes de 1400 px y ancho limitado del nombre con título completo.
- Verificado con 37 pruebas Karma y build de producción. Capturas de Chrome con
  datos simulados: catálogo a 390 y 1440 px, menú móvil y administrador a 1280/1440 px,
  sin desbordamiento horizontal. Se probaron búsqueda y cierre del menú. No hubo
  escrituras en bases ni envío de correo. No se ejecutó el backend en esta tanda.
- La revisión no certifica toda la accesibilidad ni todos los flujos del sistema.
  Quedan por mejorar el tratamiento de rutas inexistentes (hoy redirigen a Inicio),
  las cargas independientes de las secciones de Inicio y las respuestas de imágenes rotas.

## Verificación realizada

| Comprobación | Resultado |
|---|---|
| Backend: compilación y pruebas | 29 pruebas, 0 fallos y 0 errores |
| De ellas, integración PostgreSQL 16 aislado | 15 pruebas, incluyendo V1+V2, privacidad de reservas, autor de proyectos y concurrencia real |
| Frontend: Karma con ChromeHeadless | 37 pruebas correctas |
| Backend: empaquetado ejecutable Spring Boot | Correcto |
| Frontend: compilación de producción | Correcta; tamaño inicial de 624.20 kB frente a 804.31 kB antes; aún supera el objetivo de 500 kB |
| npm ci y auditoría de ejecución | Correcto; 0 avisos de ejecución, 22 moderados solo en auditoría completa de herramientas |
| Proxy de desarrollo `/api` | GET de solo lectura a `/api/avisos`: 200 JSON tanto vía Angular como directo al backend |
| TypeScript de tests | Correcto |
| git diff --check (trabajo e índice) | Correcto |

Las pruebas verifican: Flyway V1/V2, contacto de más de 255 caracteres, login por correo con mayúsculas,
alta sin sobrescritura por ID, enlace válido de 48 horas, rechazo de token vencido,
consumo del enlace, reenvío, ausencia de datos de cuenta en JSON público, sesiones revocadas,
reservas simultáneas, quitar encargado, borrados vinculados, fallo SMTP simulado, doble clic y estado
del botón en el DOM, errores HTTP que no deben confundirse con caducidad, ausencia de
falsos horarios libres, respuestas fuera de orden, privacidad de reservas,
rechazo de horarios pasados, autor de proyectos y menú móvil sin Bootstrap JS.

No se envió correo real. El servidor SMTP se simuló en pruebas unitarias y se mantuvo sin credenciales
en integración. No se escribieron datos de prueba en IER ni se borraron sus volúmenes.
El contenedor PostgreSQL temporal con tmpfs se detuvo después de verificar; `ier-postgres`
siguió encendido e intacto. El smoke test del proxy fue únicamente GET.

## Repetir las pruebas

Unitarias, sin base de datos (14 pruebas; integración se omite por defecto):

```bash
cd backend
JAVA_HOME=/opt/android-studio/jbr ./mvnw test
```

Integración, únicamente en una base temporal dedicada. El contenedor de esta ejecución fue
`ier-correcciones-test-20261103`, con almacenamiento tmpfs; no usó un volumen de IER.
Para un entorno nuevo, crear un contenedor de pruebas (el nombre y el puerto deben estar libres):

```bash
docker run --name ier-correcciones-test-20261103 --tmpfs /var/lib/postgresql/data \
  -e POSTGRES_DB=ier_test -e POSTGRES_USER=ier_test \
  -e POSTGRES_PASSWORD=isolated-test-only -p 127.0.0.1:55432:5432 -d postgres:16
```

La contraseña anterior es exclusiva de esta base temporal de pruebas. Esperar a que PostgreSQL esté listo:

```bash
docker exec ier-correcciones-test-20261103 pg_isready -U ier_test -d ier_test
```

Desde backend:

```bash
IER_RUN_DB_TESTS=true \
IER_TEST_DB_URL=jdbc:postgresql://127.0.0.1:55432/ier_test \
IER_TEST_DB_PASSWORD=isolated-test-only \
JAVA_HOME=/opt/android-studio/jbr ./mvnw clean test
```

La clase de integración rechaza cualquier URL distinta de esa base local dedicada.
Flyway crea el esquema en esa base temporal y Hibernate lo valida. Si el contenedor
existe detenido, puede arrancarse con docker start; tmpfs estará vacío.

Frontend, desde frontend:

```bash
CHROME_BIN=/usr/bin/google-chrome-stable npm test -- --watch=false --browsers=ChromeHeadless
npm run build
```

## Pendientes y límites

- Revocar la contraseña SMTP expuesta y cargar una credencial nueva mediante variables del entorno.
- Probar recepción real de correo cuando se disponga de SMTP renovado; las pruebas actuales no prueban entrega al buzón.
- Flyway V1 prepara bases vacías de Linux/Windows y V2 aplica índices. No hay baseline automática para las bases `IER` heredadas; migrar datos reales requerirá un plan y respaldo específicos.
- Añadir restricción PostgreSQL de exclusión de horarios si habrá escritores externos a la API. El bloqueo actual protege las escrituras realizadas mediante ReservaService.
- Diseñar outbox/cola persistente para reintentos de correo independientes de la petición HTTP.
- Optimizar el CSS inicial restante sin ocultar la advertencia subiendo el presupuesto; incorporar paginación para catálogos y auditoría.
- Ampliar DTO de respuesta y entrada al resto del CRUD; proyectos y reservas ya tienen los contratos nuevos.
- Añadir límites de solicitudes para login/contacto, resolver avisos moderados de herramientas sin downgrades forzados y reconsiderar JWT en localStorage con una estrategia CSRF adecuada.
- Completar la separación del resto de pestañas del panel administrativo si aumenta su complejidad; invitaciones ya está aislada.
- No se realizó despliegue ni se reinició el backend que estaba usando el usuario. Reiniciarlo con run.sh y volver a iniciar sesión para probar la versión nueva.
- Las bases nuevas de Windows y Linux aún deben crearse y configurarse por separado. No se hizo `git commit` ni `git push` ni se ejecutó el backend en Windows.

## Acciones externas

La contraseña SMTP que estuvo versionada debe revocarse desde la cuenta de correo. Eliminarla del código no la revoca ni borra el historial. No se modificará el historial Git como parte de estas correcciones.
