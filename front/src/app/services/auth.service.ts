import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.models';
import { TokenService } from './token.service';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
    // Evite d'injecter HttpClient et TokenService dans le constructeur, car cela peut causer des problèmes de dépendances circulaires.
  private readonly http = inject(HttpClient);
  private baseUrl = environment.baseUrl;
  private readonly tokenSvc = inject(TokenService);

  login(payload: LoginRequest): Observable<void> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/auth/login`, payload)
      .pipe(map((res) => this.tokenSvc.setToken(res.token)));
  }

  register(payload: RegisterRequest): Observable<void> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/auth/register`, payload)
      .pipe(map((res) => this.tokenSvc.setToken(res.token)));
  }

  logout(): void {
    this.tokenSvc.clearToken();
  }

isLoggedIn(): boolean {
  const token = this.tokenSvc.getToken();

  if (token) {
    return true;
  } else {
    return false;
  }
}
}
