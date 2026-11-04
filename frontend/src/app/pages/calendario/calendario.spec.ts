import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { CalendarioComponent } from './calendario';

describe('Carga del calendario', () => {
  it('ofrece botones enfocables para abrir las reservas de cada día', () => {
    TestBed.configureTestingModule({ imports: [CalendarioComponent], providers: [
      { provide: ApiService, useValue: { getReservas: () => of([]) } },
    ] });
    const fixture = TestBed.createComponent(CalendarioComponent);
    fixture.detectChanges();
    const boton: HTMLButtonElement = fixture.nativeElement.querySelector('button.dia-calendario');
    expect(boton.getAttribute('aria-label')).toContain('Ver reservas del');
    boton.click(); fixture.detectChanges();
    expect(fixture.componentInstance.vista).toBe('dia');
  });
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
