import { Component, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { mensajeHttp } from '../../core/http-error';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html'
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  error = '';
  entrando = signal(false);

  entrar(): void {
    if (this.entrando()) return;
    this.entrando.set(true);
    this.error = '';
    this.auth.login(this.username, this.password).pipe(finalize(() => this.entrando.set(false))).subscribe({
      next: () => {
        // Redirección según el rol autenticado
        this.router.navigate([this.auth.isAdmin ? '/admin' : this.auth.isInvestigador ? '/panel' : '/']);
      },
      error: (e) => (this.error = e instanceof HttpErrorResponse && e.status === 401
        ? 'Usuario o contraseña incorrectos'
        : mensajeHttp(e, 'No se pudo iniciar sesión')),
    });
  }
}
