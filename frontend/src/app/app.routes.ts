import { Routes } from '@angular/router';
import { Profil } from './pages/profil/profil';
import { Dokumenta } from './pages/dokumenta/dokumenta';
import { Finansije } from './pages/finansije/finansije';

export const routes: Routes = [
  { path: '', redirectTo: 'profil', pathMatch: 'full' },
  { path: 'profil', component: Profil },
  { path: 'dokumenta', component: Dokumenta },
  { path: 'finansije', component: Finansije },
];
