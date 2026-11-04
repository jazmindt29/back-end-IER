import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject } from 'rxjs';
import { ApiService, ResultadoInvitacion } from '../../core/api.service';
import { InvitacionesAdminComponent } from './invitaciones-admin';

describe('Invitaciones administrativas', () => {
  let componente: InvitacionesAdminComponent;
  let respuesta: Subject<ResultadoInvitacion>;
  let api: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    respuesta = new Subject();
    api = jasmine.createSpyObj('ApiService', ['invitarInvestigador', 'reenviarInvitacion']);
    api.invitarInvestigador.and.returnValue(respuesta);
    api.reenviarInvitacion.and.returnValue(respuesta);
    TestBed.configureTestingModule({
      imports: [InvitacionesAdminComponent],
      providers: [{ provide: ApiService, useValue: api }],
    });
    componente = TestBed.runInInjectionContext(() => new InvitacionesAdminComponent());
    componente.invitacion = { nombre: 'Prueba', email: 'test@example.org' };
  });

  it('un doble clic genera una sola solicitud y ofrece feedback de espera', () => {
    componente.invitar(); componente.invitar();
    expect(api.invitarInvestigador).toHaveBeenCalledTimes(1);
    expect(componente.enviando()).toBeTrue();
    respuesta.next({ mensaje: 'Invitación enviada', enviado: true, venceEn: '2026-11-05T12:00:00' });
    respuesta.complete();
    expect(componente.enviando()).toBeFalse();
    expect(componente.mensaje).toBe('Invitación enviada');
    expect(componente.invitacion.email).toBe('');
  });

  it('un fallo SMTP no se presenta como correo enviado y permite reenvío', () => {
    componente.invitar(true);
    respuesta.next({ mensaje: 'Guardada, correo no enviado', enviado: false, venceEn: '' });
    respuesta.complete();
    expect(api.reenviarInvitacion).toHaveBeenCalledTimes(1);
    expect(componente.mensaje).toBe('');
    expect(componente.advertencia).toContain('no enviado');
    expect(componente.invitacion.email).toBe('test@example.org');
  });

  it('restaura el botón después de un error', () => {
    componente.invitar();
    respuesta.error(new HttpErrorResponse({ status: 409, error: { mensaje: 'Usa Reenviar' } }));
    expect(componente.enviando()).toBeFalse();
    expect(componente.error).toBe('Usa Reenviar');
  });

  it('muestra Enviando y deshabilita el botón en el formulario real', async () => {
    const fixture = TestBed.createComponent(InvitacionesAdminComponent);
    fixture.componentInstance.invitacion = { nombre: 'Prueba', email: 'test@example.org' };
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
    const boton: HTMLButtonElement = fixture.nativeElement.querySelector('button.btn-primary');
    expect(boton.disabled).toBeFalse();
    boton.click(); fixture.detectChanges();
    expect(boton.disabled).toBeTrue();
    expect(boton.textContent).toContain('Enviando');
    respuesta.next({ mensaje: 'Invitación enviada', enviado: true, venceEn: '' }); respuesta.complete();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="status"]').textContent).toContain('Invitación enviada');
  });
});
