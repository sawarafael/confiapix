import { Component, signal, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './auth-page.component.scss',
})
export class LoginPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        void this.router.navigate([this.auth.homeRoute()]);
      },
      error: (err) => {
        if (err?.status === 0) {
          this.error.set('Não foi possível conectar à API. Verifique se o backend está rodando em http://localhost:8080');
        } else if (typeof err?.error === 'string' && err.error.includes('<!doctype')) {
          this.error.set('Resposta inválida da API. Confirme se o backend está em http://localhost:8080');
        } else {
          this.error.set(err?.error?.message ?? 'Falha no login');
        }
        this.loading.set(false);
      },
    });
  }
}
