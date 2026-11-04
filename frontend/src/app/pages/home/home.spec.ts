import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home';
import { ApiService } from '../../core/api.service';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [{ provide: ApiService, useValue: { getAvisos: () => of([]), getProyectosDestacados: () => of([]) } }]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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
