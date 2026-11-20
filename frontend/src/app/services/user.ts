import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDetails {
  id: number;
  firstName: string;
  middleName: string | null;
  lastName: string;
  email: string;
}

export interface Student {
  id: number;
  index: string;
}

export interface TeachingStaff {
  id: number;
}

export interface Administrator {
  id: number;
}

export interface UserProfile {
  user: UserDetails;
  students: Student[];
  staff: TeachingStaff | null;
  administrator: Administrator | null;
}

@Service()
export class User {
  private http = inject(HttpClient);

  getMyProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(
      'http://localhost:8081/api/users/me'
    );
  }
}
