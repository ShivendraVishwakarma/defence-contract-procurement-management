import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable, catchError, map, of } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.auth.currentProfile().pipe(
      map(() => true),
      catchError(() => {
        this.auth.clearProfile();
        return of(this.router.parseUrl('/login'));
      })
    );
  }
}
