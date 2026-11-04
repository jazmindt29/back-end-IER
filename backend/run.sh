#!/usr/bin/env bash
# Arranca el backend del IER.
# Carga configuración privada local sin exportaciones manuales en cada terminal.
set -Eeuo pipefail
cd "$(dirname "$0")"

if [[ ! -f .env.local ]]; then
    echo "Falta backend/.env.local. Cópialo desde .env.example y configura tu base local." >&2
    exit 1
fi

set -a
# Este archivo local es de confianza del desarrollador y está excluido de Git.
# shellcheck disable=SC1091
source .env.local
set +a

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"
exec env -u SPRING_DATASOURCE_URL -u SPRING_DATASOURCE_USERNAME -u SPRING_DATASOURCE_PASSWORD \
    ./mvnw spring-boot:run "$@"
