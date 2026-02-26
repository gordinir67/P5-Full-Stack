import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

const STORAGE_KEY = 'mdd_token';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly subject = new BehaviorSubject<string | null>(this.read());

  /** Emits current token value (null if logged out). */
  readonly token$ = this.subject.asObservable();

  getToken(): string | null {
    return this.subject.value;
  }

  setToken(token: string): void {
    localStorage.setItem(STORAGE_KEY, token);
    this.subject.next(token);
  }

  clearToken(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.subject.next(null);
  }

  private read(): string | null {
    try {
      return localStorage.getItem(STORAGE_KEY);
    } catch {
      return null;
    }
  }
}
