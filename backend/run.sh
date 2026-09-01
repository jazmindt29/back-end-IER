#!/usr/bin/env bash
# Arranca el backend del IER.
# Neutraliza las variables SPRING_DATASOURCE_* globales (de otros proyectos),
# que en Spring tienen prioridad sobre application.properties.
cd "$(dirname "$0")"
exec env -u SPRING_DATASOURCE_URL -u SPRING_DATASOURCE_USERNAME -u SPRING_DATASOURCE_PASSWORD \
    ./mvnw spring-boot:run "$@"
