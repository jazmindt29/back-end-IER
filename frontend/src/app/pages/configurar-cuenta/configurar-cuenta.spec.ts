import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, of, throwError } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ConfigurarCuentaComponent } from './configurar-cuenta';

describe('Activación por invitación', () => {
  let api: jasmine.SpyObj<ApiService>;
  let componente: ConfigurarCuentaComponent;
  beforeEach(() => {
    api = jasmine.createSpyObj('ApiService', ['validarTokenInvitacion', 'configurarCuenta']);
    TestBed.configureTestingModule({ providers: [
      { provide: ApiService, useValue: api },
      { provide: ActivatedRoute, useValue: { snapshot: { queryParamMap: { get: () => 'token-prueba' } } } },
      { provide: Router, useValue: { navigate: jasmine.createSpy() } },
    ] });
    componente = TestBed.runInInjectionContext(() => new ConfigurarCuentaComponent());
  });

  for (const status of [0, 401, 500]) {
    it('no confunde HTTP ' + status + ' con una invitación vencida', () => {
      api.validarTokenInvitacion.and.returnValue(throwError(() => new HttpErrorResponse({ status })));
      componente.ngOnInit();
      expect(componente.estado()).toBe('error');
      expect(componente.error).not.toContain('El enlace de invitación no es válido');
    });
  }

  it('presenta el motivo de rechazo real del token', () => {
    api.validarTokenInvitacion.and.returnValue(throwError(() => new HttpErrorResponse({ status: 400, error: { mensaje: 'Invitación utilizada' } })));
    componente.ngOnInit();
    expect(componente.estado()).toBe('invalido');
    expect(componente.error).toBe('Invitación utilizada');
  });

  it('activa una sola vez aunque se repita el clic', () => {
    api.validarTokenInvitacion.and.returnValue(of({ valido: true, email: 'test@example.org' }));
    const respuesta = new Subject<{ mensaje: string }>();
    api.configurarCuenta.and.returnValue(respuesta);
    componente.ngOnInit();
    componente.password = componente.confirmacion = 'password-de-prueba';
    componente.guardar(); componente.guardar();
    expect(api.configurarCuenta).toHaveBeenCalledTimes(1);
    respuesta.next({ mensaje: 'Cuenta activada' }); respuesta.complete();
    expect(componente.estado()).toBe('listo');
    expect(componente.guardando()).toBeFalse();
  });
});
