import { Routes } from '@angular/router';
import { adminGuard, investigadorGuard } from './core/guards';

export const routes: Routes = [
    { path: '', loadComponent: () => import('./pages/home/home').then(m => m.HomeComponent) },
    { path: 'quienes-somos', loadComponent: () => import('./pages/quienes-somos/quienes-somos').then(m => m.QuienesSomosComponent) },
    { path: 'laboratorios', loadComponent: () => import('./pages/laboratorios/laboratorios').then(m => m.LaboratoriosComponent) },
    { path: 'investigadores', loadComponent: () => import('./pages/investigadores/investigadores').then(m => m.InvestigadoresComponent) },
    { path: 'proyectos', title: 'Proyectos | Instituto IER', loadComponent: () => import('./pages/proyectos/proyectos').then(m => m.ProyectosComponent) },
    { path: 'calendario', loadComponent: () => import('./pages/calendario/calendario').then(m => m.CalendarioComponent) },
    { path: 'contacto', loadComponent: () => import('./pages/contacto/contacto').then(m => m.ContactoComponent) },
    { path: 'login', loadComponent: () => import('./pages/login/login').then(m => m.LoginComponent) },
    { path: 'configurar-cuenta', loadComponent: () => import('./pages/configurar-cuenta/configurar-cuenta').then(m => m.ConfigurarCuentaComponent) },
    { path: 'panel', loadComponent: () => import('./pages/panel-investigador/panel-investigador').then(m => m.PanelInvestigadorComponent), canActivate: [investigadorGuard] },
    { path: 'admin', loadComponent: () => import('./pages/panel-admin/panel-admin').then(m => m.PanelAdminComponent), canActivate: [adminGuard] },
    { path: '**', redirectTo: '' }
];
