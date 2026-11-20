import { Component, inject, OnInit, signal } from '@angular/core';
import { User, type UserProfile } from '../../services/user';

@Component({
  imports: [],
  selector: 'app-profil',
  styleUrl: './profil.scss',
  templateUrl: './profil.html',
})
export class Profil implements OnInit {
  private userService = inject(User);

  profile = signal<UserProfile | null>(null);
  error = signal('');

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
      },
      error: () => {
        this.error.set('Podaci o korisniku nisu dostupni.');
      }
    });
  }
}
