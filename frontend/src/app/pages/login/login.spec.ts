import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { AuthService } from '../../core/auth.service';
import { LoginComponent } from './login';

describe('Inicio de sesión', () => {
  it('un 401 de login se comunica como credenciales incorrectas', () => {
    const auth = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    auth.login.and.returnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    TestBed.configureTestingModule({ providers: [
      { provide: AuthService, useValue: auth },
      { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
    ] });
    const componente = TestBed.runInInjectionContext(() => new LoginComponent());
    componente.username = 'investigador@example.org';
    componente.password = 'incorrecta';
    componente.entrar();
    expect(componente.error).toBe('Usuario o contraseña incorrectos');
    expect(componente.entrando()).toBeFalse();
  });
});
