import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';
import { ProfileApiService } from '../../core/services/api.services';
import { UserProfile } from '../../core/models/api.models';
import { formatPlan, formatUserRole } from '../../core/utils/display-labels';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss',
})
export class ProfilePageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly profileApi = inject(ProfileApiService);
  protected readonly auth = inject(AuthService);

  readonly loading = signal(true);
  readonly savingProfile = signal(false);
  readonly savingPassword = signal(false);
  readonly profileError = signal<string | null>(null);
  readonly profileSuccess = signal<string | null>(null);
  readonly passwordError = signal<string | null>(null);
  readonly passwordSuccess = signal<string | null>(null);
  readonly profile = signal<UserProfile | null>(null);

  protected readonly formatRole = formatUserRole;
  protected readonly formatPlan = formatPlan;

  readonly profileForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
  });

  readonly passwordForm = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required],
  });

  ngOnInit(): void {
    this.profileApi.getProfile().subscribe({
      next: (res) => {
        this.profile.set(res.data);
        this.profileForm.patchValue({
          name: res.data.name,
          email: res.data.email,
        });
        this.loading.set(false);
      },
      error: (err) => {
        this.profileError.set(err?.error?.message ?? 'Falha ao carregar perfil');
        this.loading.set(false);
      },
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.savingProfile.set(true);
    this.profileError.set(null);
    this.profileSuccess.set(null);

    this.profileApi.updateProfile(this.profileForm.getRawValue()).subscribe({
      next: (res) => {
        this.profile.set(res.data.profile);
        this.auth.updateProfileSession(
          { name: res.data.profile.name, email: res.data.profile.email },
          res.data.token
            ? {
                token: res.data.token,
                refreshToken: res.data.refreshToken!,
                expiresIn: res.data.expiresIn!,
              }
            : undefined,
        );
        this.profileSuccess.set(res.message ?? 'Perfil atualizado com sucesso');
        this.savingProfile.set(false);
      },
      error: (err) => {
        this.profileError.set(err?.error?.message ?? 'Falha ao salvar perfil');
        this.savingProfile.set(false);
      },
    });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword, confirmPassword } = this.passwordForm.getRawValue();
    if (newPassword !== confirmPassword) {
      this.passwordError.set('A confirmação da senha não confere');
      return;
    }

    this.savingPassword.set(true);
    this.passwordError.set(null);
    this.passwordSuccess.set(null);

    this.profileApi.changePassword({ currentPassword, newPassword }).subscribe({
      next: (res) => {
        this.passwordSuccess.set(res.message ?? 'Senha alterada com sucesso');
        this.passwordForm.reset();
        this.savingPassword.set(false);
      },
      error: (err) => {
        this.passwordError.set(err?.error?.message ?? 'Falha ao alterar senha');
        this.savingPassword.set(false);
      },
    });
  }
}
