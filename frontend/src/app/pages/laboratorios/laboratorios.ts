import { Component, OnInit, inject } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { Componente, Laboratorio } from '../../core/models';

@Component({
  selector: 'app-laboratorios',
  standalone: true,
  templateUrl: './laboratorios.html'
})
export class LaboratoriosComponent implements OnInit {
  private api = inject(ApiService);

  laboratorios: Laboratorio[] = [];
  seleccionado: Laboratorio | null = null;
  materiales: Componente[] = [];

  ngOnInit(): void {
    this.api.getLaboratorios().subscribe((l) => (this.laboratorios = l));
  }

  seleccionar(lab: Laboratorio): void {
    this.seleccionado = lab;
    this.materiales = [];
    this.api.getComponentes(lab.id).subscribe((c) => (this.materiales = c));
  }
}
