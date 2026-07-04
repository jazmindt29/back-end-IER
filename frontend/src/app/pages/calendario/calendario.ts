import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { Reserva } from '../../core/models';

interface Dia {
  fecha: string;      // yyyy-MM-dd
  numero: number;
  delMes: boolean;
  esHoy: boolean;
}

function claveFecha(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

@Component({
  selector: 'app-calendario',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './calendario.html',
  styleUrl: './calendario.css'
})
export class CalendarioComponent implements OnInit {
  private api = inject(ApiService);

  readonly nombresDias = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

  vista: 'mes' | 'semana' | 'dia' = 'mes';
  cursor = new Date();               // mes/semana mostrados
  diaSeleccionado = claveFecha(new Date());
  reservasPorDia = new Map<string, Reserva[]>();
  semanas: Dia[][] = [];

  ngOnInit(): void {
    this.construirMes();
    this.api.getReservas().subscribe((reservas) => {
      this.reservasPorDia.clear();
      for (const r of reservas) {
        const lista = this.reservasPorDia.get(r.fecha) ?? [];
        lista.push(r);
        lista.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));
        this.reservasPorDia.set(r.fecha, lista);
      }
    });
  }

  get tituloMes(): Date {
    return this.cursor;
  }

  reservasDe(fecha: string): Reserva[] {
    return this.reservasPorDia.get(fecha) ?? [];
  }

  private construirMes(): void {
    const anio = this.cursor.getFullYear();
    const mes = this.cursor.getMonth();
    const primero = new Date(anio, mes, 1);
    // Lunes de la semana donde empieza el mes
    const inicio = new Date(primero);
    inicio.setDate(primero.getDate() - ((primero.getDay() + 6) % 7));

    const hoy = claveFecha(new Date());
    this.semanas = [];
    const d = new Date(inicio);
    // Después de la primera semana, d siempre está dentro del mes actual;
    // cuando pasa al siguiente, ya no quedan días del mes por pintar
    do {
      const semana: Dia[] = [];
      for (let i = 0; i < 7; i++) {
        const clave = claveFecha(d);
        semana.push({ fecha: clave, numero: d.getDate(), delMes: d.getMonth() === mes, esHoy: clave === hoy });
        d.setDate(d.getDate() + 1);
      }
      this.semanas.push(semana);
    } while (d.getMonth() === mes);
  }

  cambiarMes(delta: number): void {
    this.cursor = new Date(this.cursor.getFullYear(), this.cursor.getMonth() + delta, 1);
    this.construirMes();
  }

  seleccionarDia(fecha: string): void {
    this.diaSeleccionado = fecha;
    this.vista = 'dia';
  }

  // Días (lunes a domingo) de la semana del día seleccionado, para la vista semanal
  get diasSemana(): Dia[] {
    const base = new Date(this.diaSeleccionado + 'T00:00:00');
    const lunes = new Date(base);
    lunes.setDate(base.getDate() - ((base.getDay() + 6) % 7));
    const hoy = claveFecha(new Date());
    const dias: Dia[] = [];
    const d = new Date(lunes);
    for (let i = 0; i < 7; i++) {
      const clave = claveFecha(d);
      dias.push({ fecha: clave, numero: d.getDate(), delMes: true, esHoy: clave === hoy });
      d.setDate(d.getDate() + 1);
    }
    return dias;
  }

  cambiarSemana(delta: number): void {
    const base = new Date(this.diaSeleccionado + 'T00:00:00');
    base.setDate(base.getDate() + delta * 7);
    this.diaSeleccionado = claveFecha(base);
  }

  cambiarDia(delta: number): void {
    const base = new Date(this.diaSeleccionado + 'T00:00:00');
    base.setDate(base.getDate() + delta);
    this.diaSeleccionado = claveFecha(base);
  }

  hora(h: string): string {
    return h.slice(0, 5); // "10:00:00" -> "10:00"
  }
}
