import { Component, inject } from '@angular/core';
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

  enviar(): void {
    this.error = '';
    this.api.enviarContacto(this.modelo).subscribe({
      next: () => {
        this.enviado = true;
        this.modelo = { nombre: '', email: '', asunto: '', mensaje: '' };
      },
      error: (e) => (this.error = e.error?.mensaje ?? 'No se pudo enviar el mensaje'),
    });
  }
}
