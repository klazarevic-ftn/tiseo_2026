import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DocumentDetails {
  id: number;
  fileName: string;
  userId: number;
}

@Service()
export class DocumentService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8086/api/documents';

  findAll(userId?: number): Observable<DocumentDetails[]> {
    if (userId !== undefined) {
      return this.http.get<DocumentDetails[]>(this.url, {
        params: { userId }
      });
    }

    return this.http.get<DocumentDetails[]>(this.url);
  }

  upload(file: File): Observable<DocumentDetails> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<DocumentDetails>(this.url, formData);
  }

  download(id: number): Observable<Blob> {
    return this.http.get(`${this.url}/${id}/download`, {
      responseType: 'blob'
    });
  }

  update(id: number, fileName: string): Observable<DocumentDetails> {
    return this.http.put<DocumentDetails>(`${this.url}/${id}`, {
      fileName
    });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
