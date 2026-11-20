import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { UserDetails } from './user';

export interface StudentDetails {
  id: number;
  index: string;
  user: UserDetails;
}

@Service()
export class StudentService {
  private http = inject(HttpClient);

  search(search: string): Observable<StudentDetails[]> {
    return this.http.get<StudentDetails[]>(
      'http://localhost:8081/api/students/search',
      {
        params: { search }
      }
    );
  }
}
