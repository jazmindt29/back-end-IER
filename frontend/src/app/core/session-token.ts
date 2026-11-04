interface Claims {
  sub: string;
  exp: number;
  rol?: string;
  userId?: number;
  investigadorId?: number;
}

/** Solo interpreta la sesión para la UI; la autorización real pertenece al servidor. */
export function sesionActual(): { token: string; claims: Claims } | null {
  const token = localStorage.getItem('ier_token');
  if (!token) return null;
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const bytes = Uint8Array.from(atob(base64), (c) => c.charCodeAt(0));
    const claims = JSON.parse(new TextDecoder().decode(bytes)) as Claims;
    if (typeof claims.exp === 'number' && claims.exp * 1000 > Date.now()
        && typeof claims.sub === 'string') return { token, claims };
  } catch { /* Una sesión ilegible se descarta igual que una vencida. */ }
  localStorage.removeItem('ier_token');
  return null;
}
