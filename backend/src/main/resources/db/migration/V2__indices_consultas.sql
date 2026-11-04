-- Consultas de disponibilidad, perfiles y enlaces. Las FK no crean estos índices.
CREATE INDEX idx_reservas_laboratorio_fecha ON reservas_laboratorio (laboratorio_id, fecha);
CREATE INDEX idx_reservas_investigador_fecha ON reservas_laboratorio (investigador_id, fecha DESC);
CREATE UNIQUE INDEX idx_users_token_invitacion ON users (token_invitacion)
    WHERE token_invitacion IS NOT NULL;
CREATE INDEX idx_componentes_laboratorio ON componentes (laboratorio_id);
CREATE INDEX idx_proyecto_investigadores_investigador ON proyecto_investigadores (investigador_id);
CREATE INDEX idx_laboratorios_encargado ON laboratorios (encargado_id);
CREATE INDEX idx_avisos_autor ON avisos (autor_id);
