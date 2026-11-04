import { TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { Componente } from '../../core/models';
import { LaboratoriosComponent } from './laboratorios';

describe('Selección de laboratorios', () => {
  it('descarta respuestas de un laboratorio que ya no está seleccionado', () => {
    const anterior = new Subject<Componente[]>();
    const actual = new Subject<Componente[]>();
    const api = { getComponentes: (id: number) => id === 1 ? anterior : actual };
    TestBed.configureTestingModule({ providers: [{ provide: ApiService, useValue: api }] });
    const componente = TestBed.runInInjectionContext(() => new LaboratoriosComponent());
    componente.seleccionar({ id: 1, nombre: 'Anterior' });
    componente.seleccionar({ id: 2, nombre: 'Actual' });
    actual.next([{ id: 2, nombre: 'Material actual' }]); actual.complete();
    anterior.next([{ id: 1, nombre: 'Material anterior' }]); anterior.complete();
    expect(componente.materiales[0].nombre).toBe('Material actual');
    expect(componente.cargandoMateriales()).toBeFalse();
  });
});
