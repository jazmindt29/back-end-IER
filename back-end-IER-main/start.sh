#!/usr/bin/env bash
# Levanta todo el proyecto IER: PostgreSQL (Docker) + backend (:8080) + frontend (:4200).
# Ctrl+C detiene backend y frontend (el contenedor de BD queda corriendo, es barato).
set -e
cd "$(dirname "$0")"

# ---------- 1. Base de datos ----------
if ! docker ps --format '{{.Names}}' | grep -q '^ier-postgres$'; then
    docker start ier-postgres 2>/dev/null \
        || docker run --name ier-postgres -e POSTGRES_PASSWORD=jazmin \
             -e POSTGRES_DB=IER -p 5432:5432 -d postgres:16
fi
until docker exec ier-postgres pg_isready -U postgres -q 2>/dev/null; do sleep 1; done
echo "✓ PostgreSQL listo (contenedor ier-postgres)"

# ---------- 2. Backend ----------
if curl -s -o /dev/null http://localhost:8080/api/avisos; then
    echo "✓ Backend ya estaba corriendo en :8080"
else
    (cd backend && exec ./run.sh) > /tmp/ier-backend.log 2>&1 &
    echo "  Backend arrancando (log: /tmp/ier-backend.log)..."
    for i in $(seq 1 60); do
        sleep 2
        if curl -s -o /dev/null http://localhost:8080/api/avisos; then break; fi
        if [ "$i" = 60 ]; then
            echo "✗ El backend no respondió; últimas líneas del log:" >&2
            tail -20 /tmp/ier-backend.log >&2
            exit 1
        fi
    done
    echo "✓ Backend listo en http://localhost:8080"
fi

# Al salir (Ctrl+C o fin), detener los procesos que este script haya lanzado
trap 'trap - INT TERM EXIT; kill 0 2>/dev/null' INT TERM EXIT

# ---------- 3. Frontend (primer plano) ----------
cd frontend
[ -d node_modules ] || npm install
echo "✓ Abriendo frontend en http://localhost:4200 (Ctrl+C para detener todo)"
npm start
