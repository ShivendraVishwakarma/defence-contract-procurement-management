import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../shared/auth.service';

@Component({ selector: 'app-login', templateUrl: './login.component.html', styleUrls: ['./login.component.css'] })
export class LoginComponent {
  busy = false;
  errorMessage = '';
  readonly form = this.formBuilder.nonNullable.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.maxLength(72)]]
  });

  constructor(private readonly formBuilder: FormBuilder, private readonly auth: AuthService, private readonly router: Router) {}

  submit(): void {
    this.errorMessage = '';
    if (this.form.invalid || this.busy) { this.form.markAllAsTouched(); return; }
    this.busy = true;
    this.auth.login(this.form.getRawValue()).pipe(finalize(() => this.busy = false)).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: (error: HttpErrorResponse) => {
        this.errorMessage = error.status === 0
          ? 'The sign-in service is unavailable. Please try again shortly.'
          : error.status === 401
            ? 'The username or password is incorrect.'
            : 'We could not complete sign in. Check your details and try again.';
      }
    });
  }
}
