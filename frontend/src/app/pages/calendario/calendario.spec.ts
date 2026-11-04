import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { CalendarioComponent } from './calendario';

describe('Carga del calendario', () => {
  it('termina la espera y comunica un error al fallar la disponibilidad', () => {
    TestBed.configureTestingModule({ providers: [{ provide: ApiService, useValue: {
      getReservas: () => throwError(() => new HttpErrorResponse({ status: 500 })),
    } }] });
    const componente = TestBed.runInInjectionContext(() => new CalendarioComponent());
    componente.ngOnInit();
    expect(componente.cargando()).toBeFalse();
    expect(componente.cargado).toBeFalse();
    expect(componente.error).toContain('servidor');
    const fixture = TestBed.createComponent(CalendarioComponent);
    fixture.componentInstance.vista = 'dia';
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('todos los laboratorios están libres');
  });
});
