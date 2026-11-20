import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { keycloak } from './keycloak';
import { User, type UserProfile } from './services/user';

@Component({
  imports: [RouterLink, RouterOutlet, ButtonModule, MenuModule],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App implements OnInit {
  protected readonly title = signal('fin');

  private userService = inject(User);

  profile = signal<UserProfile | null>(null);
  profileError = signal(false);

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
      },
      error: () => {
        this.profileError.set(true);
      }
    });
  }

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
        {
          label: 'Dokumenta',
          routerLink: '/dokumenta',
          visible:
            keycloak.hasRealmRole('STUDENT') ||
            keycloak.hasRealmRole('ADMINISTRATOR')
        },
        {
          label: 'Finansijska kartica',
          routerLink: '/finansije',
          visible:
            keycloak.hasRealmRole('STUDENT') ||
            keycloak.hasRealmRole('ADMINISTRATOR')
        }
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

  logout(): void {
    void keycloak.logout({
      redirectUri: window.location.origin
    });
  }
}
