import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';
import { LoginCredentials, UserProfile } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly profileSubject = new BehaviorSubject<UserProfile | null>(null);
  readonly profile$ = this.profileSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginCredentials): Observable<UserProfile> {
    return this.http.get<{ token: string }>('/api/auth/csrf', { withCredentials: true }).pipe(
      switchMap(() => this.http.post<UserProfile>('/api/auth/login', credentials, { withCredentials: true })),
      tap(profile => this.profileSubject.next(profile))
    );
  }

  currentProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>('/api/auth/me', { withCredentials: true }).pipe(
      tap(profile => this.profileSubject.next(profile))
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/auth/logout', {}, { withCredentials: true }).pipe(
      tap(() => this.profileSubject.next(null))
    );
  }

  clearProfile(): void { this.profileSubject.next(null); }
}
