import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';

@Component({
  imports: [RouterLink, RouterOutlet, ButtonModule, MenuModule],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('fin');
  expandedSections = {
    indeks: true,
    studije: true,
    ispiti: true
  };
  menuItems: MenuItem[] = [
    {
      id: 'indeks',
      label: 'Indeks',
      toggleable: true,
      items: [
        { label: 'Profil', routerLink: '/profil' },
        { label: 'Dokumenta', routerLink: '/dokumenta' },
        { label: 'Finansijska kartica', routerLink: '/finansije' }
      ]
    },
    {
      id: 'studije',
      label: 'Studije',
      toggleable: true,
      items: [
        { label: 'Raspored nastave', routerLink: '/raspored' }
      ]
    },
    {
      id: 'ispiti',
      label: 'Ispiti',
      toggleable: true,
      items: [
        { label: 'Položeni predmeti', routerLink: '/ispiti/polozeni' },
        { label: 'Nepoloženi predmeti', routerLink: '/ispiti/nepolozeni' },
        { label: 'Prijava ispita', routerLink: '/ispiti/prijava' }
      ]
    }
  ];
}
