import { Component, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { mensajeHttp } from '../../core/http-error';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './contacto.html'
})
export class ContactoComponent {
  private api = inject(ApiService);

  // Validación del cliente: el correo debe ser del Gmail institucional
  readonly gmailPattern = '^[a-zA-Z0-9._%+\\-]+@gmail\\.com$';

  modelo = { nombre: '', email: '', asunto: '', mensaje: '' };
  enviado = false;
  error = '';
  enviando = signal(false);

  enviar(): void {
    if (this.enviando()) return;
    this.enviando.set(true);
    this.enviado = false;
    this.error = '';
    this.api.enviarContacto(this.modelo).pipe(finalize(() => this.enviando.set(false))).subscribe({
      next: () => {
        this.enviado = true;
        this.modelo = { nombre: '', email: '', asunto: '', mensaje: '' };
      },
      error: (e) => (this.error = mensajeHttp(e, 'No se pudo registrar el mensaje')),
    });
  }
}
