#!/bin/bash
set -euo pipefail

# Render entrega DATABASE_URL como postgresql://usuario:clave@host:puerto/base.
# Spring necesita el formato JDBC y las credenciales por separado.
if [ -n "${DATABASE_URL:-}" ] && [ -z "${IER_DB_URL:-}" ]; then
    IER_DB_URL="$(printf '%s' "$DATABASE_URL" | sed -E 's#^postgres(ql)?://([^@/]+@)?#jdbc:postgresql://#')"
    export IER_DB_URL
fi

: "${BACKEND_URL:=http://127.0.0.1:8081}"
export BACKEND_URL

envsubst '${BACKEND_URL}' < /app/nginx.conf.template > /etc/nginx/conf.d/default.conf

exec /usr/bin/supervisord -c /etc/supervisor/supervisord.conf
