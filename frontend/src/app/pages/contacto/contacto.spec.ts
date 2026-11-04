import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ContactoComponent } from './contacto';

describe('Formulario público de contacto', () => {
  it('acepta correos institucionales que no sean Gmail y rechaza correos inválidos', async () => {
    TestBed.configureTestingModule({ imports: [ContactoComponent], providers: [
      { provide: ApiService, useValue: { enviarContacto: () => of({}) } },
    ] });
    const fixture = TestBed.createComponent(ContactoComponent);
    fixture.componentInstance.modelo = { nombre: 'Prueba', email: 'persona@udg.mx', asunto: '', mensaje: 'Consulta' };
    fixture.detectChanges(); await fixture.whenStable(); fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('button').disabled).toBeFalse();
    fixture.componentInstance.modelo.email = 'correo-invalido';
    fixture.detectChanges(); await fixture.whenStable(); fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('button').disabled).toBeTrue();
  });
});
