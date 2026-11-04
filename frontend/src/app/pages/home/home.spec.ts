import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home';
import { ApiService } from '../../core/api.service';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [provideRouter([]), { provide: ApiService, useValue: { getAvisos: () => of([]), getProyectosDestacados: () => of([]) } }]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('permite ir al catálogo aunque no haya proyectos destacados', () => {
    expect(fixture.nativeElement.querySelector('a[href="/proyectos"]').textContent).toContain('Ver todos');
  });

  it('presenta error de conexión en vez de fingir que no hay contenido', () => {
    spyOn(TestBed.inject(ApiService), 'getAvisos').and.returnValue(throwError(() => new HttpErrorResponse({ status: 0 })));
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.cargando()).toBeFalse();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('conectar');
    expect(fixture.nativeElement.textContent).not.toContain('No hay avisos publicados');
  });
});
