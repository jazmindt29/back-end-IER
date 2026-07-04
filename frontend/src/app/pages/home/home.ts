import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { Aviso, Proyecto } from '../../core/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './home.html'
})
export class HomeComponent implements OnInit {
  private api = inject(ApiService);

  avisos: Aviso[] = [];
  destacados: Proyecto[] = [];
  expandidos = new Set<string>();

  ngOnInit(): void {
    this.api.getAvisos().subscribe((a) => (this.avisos = a));
    this.api.getProyectosDestacados().subscribe((p) => (this.destacados = p));
  }

  toggle(clave: string): void {
    if (this.expandidos.has(clave)) {
      this.expandidos.delete(clave);
    } else {
      this.expandidos.add(clave);
    }
  }

  expandido(clave: string): boolean {
    return this.expandidos.has(clave);
  }
}
