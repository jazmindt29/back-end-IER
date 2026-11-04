import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { of, Subject, throwError } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { Proyecto } from '../../core/models';
import { routes } from '../../app.routes';
import { ProyectosComponent } from './proyectos';

describe('Catálogo público de proyectos', () => {
  let api: jasmine.SpyObj<ApiService>;
  const proyectos: Proyecto[] = [
    { id: 1, titulo: 'Agua limpia', esDestacado: true, descripcion: 'Tratamiento de agua' },
    { id: 2, titulo: 'Energía solar', esDestacado: false, investigadores: [{ id: 3, nombre: 'Díaz' }] },
  ];

  beforeEach(() => {
    api = jasmine.createSpyObj('ApiService', ['getProyectos']);
    api.getProyectos.and.returnValue(of(proyectos));
    TestBed.configureTestingModule({ imports: [ProyectosComponent], providers: [{ provide: ApiService, useValue: api }] });
  });

  it('muestra destacados y no destacados sin requerir sesión', () => {
    const fixture = TestBed.createComponent(ProyectosComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Agua limpia');
    expect(fixture.nativeElement.textContent).toContain('Energía solar');
    expect(fixture.nativeElement.textContent).toContain('Díaz');
    expect(api.getProyectos).toHaveBeenCalledTimes(1);
    expect(routes.find(r => r.path === 'proyectos')?.canActivate).toBeUndefined();
    expect(routes.find(r => r.path === 'proyectos')?.loadComponent).toBeDefined();
  });

  it('busca sin distinguir acentos y permite recuperar el listado completo', () => {
    const fixture = TestBed.createComponent(ProyectosComponent);
    fixture.detectChanges();
    fixture.componentInstance.busqueda.set('diaz');
    expect(fixture.componentInstance.visibles().map(p => p.id)).toEqual([2]);
    fixture.componentInstance.busqueda.set('inexistente');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No hay proyectos que coincidan');
    fixture.nativeElement.querySelector('button').click();
    expect(fixture.componentInstance.visibles().length).toBe(2);
  });

  it('no presenta una carga pendiente como un catálogo vacío', () => {
    const respuesta = new Subject<Proyecto[]>();
    api.getProyectos.and.returnValue(respuesta);
    const fixture = TestBed.createComponent(ProyectosComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Cargando proyectos');
    expect(fixture.nativeElement.textContent).not.toContain('No hay proyectos publicados');
    respuesta.next([]); respuesta.complete(); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No hay proyectos publicados');
  });

  it('informa un fallo y permite reintentar sin recargar toda la aplicación', () => {
    api.getProyectos.and.returnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    const fixture = TestBed.createComponent(ProyectosComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="alert"]')).not.toBeNull();
    expect(fixture.nativeElement.textContent).not.toContain('No hay proyectos publicados');
    api.getProyectos.and.returnValue(of(proyectos));
    fixture.nativeElement.querySelector('button').click(); fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="alert"]')).toBeNull();
    expect(fixture.nativeElement.querySelectorAll('article').length).toBe(2);
  });
});
