import { TestBed } from '@angular/core/testing';
import { Subject, of } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { PanelAdminComponent } from './panel-admin';

describe('Panel administrativo', () => {
  let componente: PanelAdminComponent;
  let api: jasmine.SpyObj<ApiService>;
  beforeEach(() => {
    api = jasmine.createSpyObj('ApiService', ['crearAviso', 'actualizarAviso', 'getAvisos']);
    api.getAvisos.and.returnValue(of([]));
    TestBed.configureTestingModule({ imports: [PanelAdminComponent], providers: [{ provide: ApiService, useValue: api }] });
    componente = TestBed.runInInjectionContext(() => new PanelAdminComponent());
  });

  it('protege también los formularios CRUD de envíos duplicados', () => {
    const guardado = new Subject<any>();
    api.crearAviso.and.returnValue(guardado);
    componente.avisoEdit = { titulo: 'Prueba', contenido: 'Prueba' };
    componente.guardarAviso(); componente.guardarAviso();
    expect(api.crearAviso).toHaveBeenCalledTimes(1);
    expect(componente.guardando()).toBeTrue();
    guardado.next({ id: 1 }); guardado.complete();
    expect(componente.guardando()).toBeFalse();
    expect(componente.avisoEdit).toBeNull();
  });

  it('no borra un segundo borrador abierto durante un guardado', () => {
    const guardado = new Subject<any>();
    api.actualizarAviso.and.returnValue(guardado);
    componente.pestania = 'avisos';
    componente.avisoEdit = { id: 1, titulo: 'A', contenido: 'A' };
    componente.guardarAviso();
    componente.avisoEdit = { id: 2, titulo: 'B', contenido: 'B' };
    guardado.next({ id: 1 }); guardado.complete();
    expect(componente.avisoEdit?.id).toBe(2);
  });
});
