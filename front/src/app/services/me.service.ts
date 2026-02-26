import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import type { SubscriptionDto } from '../models/subscription.models';
import type { UpdateUserRequest, UserDto } from '../models/user.models';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class MeService {
  private readonly http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getMe(): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.baseUrl}/me`);
  }

  updateMe(payload: UpdateUserRequest): Observable<UserDto> {
    return this.http.put<UserDto>(`${this.baseUrl}/me`, payload);
  }

  subscriptions(): Observable<SubscriptionDto[]> {
    return this.http.get<SubscriptionDto[]>(`${this.baseUrl}/me/subscriptions`);
  }
}
