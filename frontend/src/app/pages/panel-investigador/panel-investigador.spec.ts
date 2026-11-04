import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, of, throwError } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { Reserva } from '../../core/models';
import { PanelInvestigadorComponent } from './panel-investigador';

describe('Panel de investigador', () => {
  let api: jasmine.SpyObj<ApiService>;
  let componente: PanelInvestigadorComponent;
  beforeEach(() => {
    api = jasmine.createSpyObj('ApiService', ['getMiPerfil', 'getLaboratorios', 'getReservas', 'crearReserva']);
    api.getMiPerfil.and.returnValue(of({ id: 1, nombre: 'Prueba' }));
    api.getLaboratorios.and.returnValue(of([]));
    api.getReservas.and.returnValue(of([]));
    TestBed.configureTestingModule({ providers: [
      { provide: ApiService, useValue: api }, { provide: AuthService, useValue: { investigadorId: 1 } },
    ] });
    componente = TestBed.runInInjectionContext(() => new PanelInvestigadorComponent());
  });

  it('un error de perfil no deja una carga perpetua', () => {
    api.getMiPerfil.and.returnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    componente.ngOnInit();
    expect(componente.cargandoPerfil()).toBeFalse();
    expect(componente.errorPerfil).toContain('servidor');
  });

  it('evita duplicar la solicitud de reserva', () => {
    const respuesta = new Subject<Reserva>();
    api.crearReserva.and.returnValue(respuesta);
    componente.nuevaReserva = { laboratorioId: 1, fecha: '2026-12-01', horaInicio: '10:00', horaFin: '11:00' };
    componente.agendar(); componente.agendar();
    expect(api.crearReserva).toHaveBeenCalledTimes(1);
    respuesta.next({ id: 1, fecha: '2026-12-01', horaInicio: '10:00', horaFin: '11:00', laboratorio: { id: 1, nombre: 'Prueba' } });
    respuesta.complete();
    expect(componente.guardandoReserva()).toBeFalse();
    expect(componente.mensajeReserva).toContain('registrada');
  });

  it('descarta una lista anterior después de crear una reserva', () => {
    const anterior = new Subject<Reserva[]>();
    const actual = new Subject<Reserva[]>();
    api.getReservas.and.returnValues(anterior, actual);
    api.crearReserva.and.returnValue(of({ id: 2, fecha: '2026-12-01', horaInicio: '10:00', horaFin: '11:00', laboratorio: { id: 1, nombre: 'Prueba' } }));
    componente.ngOnInit();
    componente.agendar();
    actual.next([{ id: 2, fecha: '2026-12-01', horaInicio: '10:00', horaFin: '11:00', laboratorio: { id: 1, nombre: 'Prueba' } }]);
    anterior.next([]);
    expect(componente.reservas.map(r => r.id)).toEqual([2]);
  });
});
