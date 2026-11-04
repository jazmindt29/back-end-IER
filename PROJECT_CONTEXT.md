# Contexto técnico IER

Actualizado: 2026-11-03. La documentación anterior describía scripts eliminados y flujos
duplicados; esta versión corresponde al código corregido.

## Arquitectura

Monorepo: Angular 20 standalone en frontend, API Spring Boot 4.0.1/Java 17 en backend,
PostgreSQL 16. Capas principales: controladores HTTP, servicios transaccionales,
repositorios Spring Data y entidades JPA. Las entradas de contacto, reservas,
proyectos y alta de cuentas usan DTO validados. Los demás POST rechazan IDs explícitos
de creación. Las reservas públicas devuelven solo horario y referencias mínimas.

La navegación pública incluye `/proyectos`: consulta el catálogo completo y permite
buscar sin sesión. Inicio conserva su sección de proyectos destacados y enlaza al
catálogo. Las fechas de la aplicación usan español de México.

## Autenticación

JWT HS256 con issuer ier-api y duración de ocho horas. Además de firma y expiración,
se verifica que la cuenta exista, esté habilitada y conserve su rol. La UI nunca decide
la autorización real. El interceptor no envía JWT a servidores externos ni a login,
validación de invitación o activación; el POST de invitación y reenvío sí exige sesión admin.

## Invitaciones

1. Admin envía nombre y correo (normalizado a minúsculas).
2. AuthService crea una cuenta inactiva, token UUID, expiración de 48 horas y perfil.
3. La transacción confirma los datos.
4. AuthController solicita el envío SMTP y devuelve mensaje, enviado y venceEn.
5. Sin SMTP, el resultado indica guardado sin envío; el token no se expone.
6. La validación pública comprueba el token sin consumirlo.
7. La activación bloquea la fila, cifra la contraseña y consume el token.
8. Reenviar actualiza token y vigencia de una cuenta pendiente; no crea otra cuenta.

El frontend llama a `/api` bajo su mismo origen. En desarrollo `ng serve` usa un
proxy local; fuera de `dev`, sin `APP_FRONTEND_URL` HTTP(S) explícita el backend no
envía invitaciones con enlaces potencialmente rotos.

Rutas:
- POST /api/auth/login
- POST /api/auth/invitaciones (ADMIN/ADMINISTRADOR)
- POST /api/auth/invitaciones/reenviar (ADMIN/ADMINISTRADOR)
- GET /api/auth/invitaciones/{token} (público)
- POST /api/auth/configurar-cuenta (público)

Los endpoints duplicados /api/usuarios/validar-token y /api/usuarios/activar-cuenta
se retiraron. El frontend no los utilizaba. POST /api/usuarios queda para altas administrativas
con contraseña explícita; los investigadores se crean mediante invitación.

## Integridad

- Reservar bloquea la fila del laboratorio dentro de una transacción antes de consultar horarios.
  Protege solicitudes concurrentes de todas las instancias de esta API que usen el servicio.
- No se admiten IDs de reserva/contacto suministrados para actualizar mediante un POST.
- El investigador autenticado solo reserva a su propio nombre.
- No se acepta una hora de inicio pasada aunque la fecha sea hoy.
- Los borrados con referencias se rechazan con 409; no se borran reservas o proyectos en cascada.
- Se puede quitar un encargado asignando null.
- Cambios de proyecto, laboratorio y perfil comparten transacción con su auditoría.
- El JSON público no expone la cuenta vinculada de investigadores ni el usuario autor de avisos.
- El GET público de reservas omite correo y perfil completo del investigador.
- Proyecto usa IDs de investigadores validados y establece createdBy desde la sesión admin.
- Open-in-view está desactivado; proyectos carga investigadores mediante EntityGraph.

## Configuración y operación

Ver [README.md](README.md) y [backend/.env.example](backend/.env.example). Para
desarrollo, `backend/run.sh` y `backend/run.ps1` cargan `backend/.env.local`; ese
archivo está ignorado por Git. Las variables ya no se exportan a mano en cada terminal.
Las bases de desarrollo nuevas de Linux y Windows tienen datos independientes. Flyway V1
crea el esquema y V2 añade índices en ambas; Hibernate lo valida. `dev` ya no usa
`ddl-auto=update`.
El perfil `dev` aún puede migrar contraseñas antiguas; no es de solo lectura.
El administrador inicial requiere variables externas y perfil `dev` o `bootstrap`.
No aplicar V1 sobre las bases `IER` antiguas: no se habilitó baseline automática.

## Pruebas y deuda técnica

Ver el registro verificable en [docs/PLAN_CORRECCIONES.md](docs/PLAN_CORRECCIONES.md).
La CI de GitHub usa PostgreSQL de servicio, compilación Angular, Karma y auditoría
de dependencias de ejecución. No se implementó una cola persistente de correo,
paginación global ni una baseline
para bases preexistentes.
Esos cambios requieren un trabajo separado de diseño y migración, documentado en el registro.
