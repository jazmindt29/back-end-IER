import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { QuienesSomosComponent } from './pages/quienes-somos/quienes-somos';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'quienes-somos', component: QuienesSomosComponent },
    { path: '**', redirectTo: '' }
];