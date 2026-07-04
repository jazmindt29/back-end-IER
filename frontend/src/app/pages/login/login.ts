import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
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

  entrar(): void {
    this.error = '';
    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        // Redirección según el rol autenticado
        this.router.navigate([this.auth.isAdmin ? '/admin' : this.auth.isInvestigador ? '/panel' : '/']);
      },
      error: (e) => (this.error = e.error?.mensaje ?? 'No se pudo iniciar sesión'),
    });
  }
}
