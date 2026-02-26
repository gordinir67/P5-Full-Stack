import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import type { ThemeDto } from '../models/theme.models';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  list(): Observable<ThemeDto[]> {
    return this.http.get<ThemeDto[]>(`${this.baseUrl}/themes`);
  }

  subscribe(themeId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/themes/${themeId}/subscribe`, null);
  }

  unsubscribe(themeId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/themes/${themeId}/subscribe`);
  }
}
