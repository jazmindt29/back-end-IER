# Frontend — Instituto IER

SPA en Angular 20 (componentes standalone) con Bootstrap 5. Consume la API del backend en `http://localhost:8080/api` (configurable en `src/app/core/api.constants.ts`).

## Desarrollo

```bash
npm install
npm start        # http://localhost:4200, recarga automática
```

El backend debe estar corriendo (ver el README de la raíz del repo).

## Estructura

```
src/app/
├── core/          # modelos, ApiService, AuthService (JWT), interceptor, guards
├── components/    # navbar (según sesión/rol), footer
└── pages/         # una carpeta por ruta (públicas, login, paneles privados)
```

Rutas y guards en `src/app/app.routes.ts`; providers en `src/app/app.config.ts`.

## Otros comandos

```bash
npm run build    # build de producción en dist/
npm test         # tests unitarios con Karma
```
