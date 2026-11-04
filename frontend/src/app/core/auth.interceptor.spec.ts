import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { API_URL } from './api.constants';

describe('authInterceptor', () => {
  let http: HttpClient;
  let backend: HttpTestingController;
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting()] });
    http = TestBed.inject(HttpClient);
    backend = TestBed.inject(HttpTestingController);
  });
  afterEach(() => { backend.verify(); localStorage.clear(); });

  function token(exp: number) {
    return 'header.' + btoa(JSON.stringify({ sub: 'admin', exp })) + '.signature';
  }

  it('no adjunta una sesión vieja a la validación o activación pública', () => {
    localStorage.setItem('ier_token', token(1));
    http.get(API_URL + '/auth/invitaciones/test-token').subscribe();
    const validation = backend.expectOne(API_URL + '/auth/invitaciones/test-token');
    expect(validation.request.headers.has('Authorization')).toBeFalse();
    validation.flush({ valido: true });
    http.post(API_URL + '/auth/configurar-cuenta', {}).subscribe();
    const activation = backend.expectOne(API_URL + '/auth/configurar-cuenta');
    expect(activation.request.headers.has('Authorization')).toBeFalse();
    activation.flush({});
  });

  it('conserva la autenticación para invitar y reenviar', () => {
    const jwt = token(Date.now() / 1000 + 3600);
    localStorage.setItem('ier_token', jwt);
    for (const path of ['/auth/invitaciones', '/auth/invitaciones/reenviar']) {
      http.post(API_URL + path, {}).subscribe();
      const req = backend.expectOne(API_URL + path);
      expect(req.request.headers.get('Authorization')).toBe('Bearer ' + jwt);
      req.flush({});
    }
  });

  it('descarta JWT vencidos y no filtra tokens hacia otros servidores', () => {
    localStorage.setItem('ier_token', token(1));
    http.get(API_URL + '/avisos').subscribe();
    const expired = backend.expectOne(API_URL + '/avisos');
    expect(expired.request.headers.has('Authorization')).toBeFalse();
    expect(localStorage.getItem('ier_token')).toBeNull();
    expired.flush([]);
    localStorage.setItem('ier_token', token(Date.now() / 1000 + 3600));
    http.get('https://example.invalid/api').subscribe();
    const external = backend.expectOne('https://example.invalid/api');
    expect(external.request.headers.has('Authorization')).toBeFalse();
    external.flush({});
  });

  it('descarta una sesión revocada cuando el servidor responde 401', () => {
    localStorage.setItem('ier_token', token(Date.now() / 1000 + 3600));
    http.get(API_URL + '/usuarios').subscribe({ error: () => {} });
    backend.expectOne(API_URL + '/usuarios').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(localStorage.getItem('ier_token')).toBeNull();
  });
});
