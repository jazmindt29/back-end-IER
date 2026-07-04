import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { QuienesSomosComponent } from './pages/quienes-somos/quienes-somos';
import { LaboratoriosComponent } from './pages/laboratorios/laboratorios';
import { InvestigadoresComponent } from './pages/investigadores/investigadores';
import { CalendarioComponent } from './pages/calendario/calendario';
import { ContactoComponent } from './pages/contacto/contacto';
import { LoginComponent } from './pages/login/login';
import { ConfigurarCuentaComponent } from './pages/configurar-cuenta/configurar-cuenta';
import { PanelInvestigadorComponent } from './pages/panel-investigador/panel-investigador';
import { PanelAdminComponent } from './pages/panel-admin/panel-admin';
import { adminGuard, investigadorGuard } from './core/guards';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'quienes-somos', component: QuienesSomosComponent },
    { path: 'laboratorios', component: LaboratoriosComponent },
    { path: 'investigadores', component: InvestigadoresComponent },
    { path: 'calendario', component: CalendarioComponent },
    { path: 'contacto', component: ContactoComponent },
    { path: 'login', component: LoginComponent },
    { path: 'configurar-cuenta', component: ConfigurarCuentaComponent },
    { path: 'panel', component: PanelInvestigadorComponent, canActivate: [investigadorGuard] },
    { path: 'admin', component: PanelAdminComponent, canActivate: [adminGuard] },
    { path: '**', redirectTo: '' }
];
