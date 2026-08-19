import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { StoneApiService } from '../../core/services/api.services';
import { StoneCredentials } from '../../core/models/api.models';

@Component({
  selector: 'app-stone-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './stone-page.component.html',
  styleUrl: './stone-page.component.scss',
})
export class StonePageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly stoneApi = inject(StoneApiService);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly testing = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly credentials = signal<StoneCredentials | null>(null);

  readonly form = this.fb.nonNullable.group({
    authMode: ['API_KEY' as 'OPEN_BANKING' | 'API_KEY', Validators.required],
    businessModel: ['GATEWAY' as 'GATEWAY' | 'SUBACQUIRER'],
    clientSecret: [''],
    accountId: ['', Validators.required],
    merchantId: [''],
  });

  ngOnInit(): void {
    this.stoneApi.getCredentials().subscribe({
      next: (res) => {
        this.credentials.set(res.data);
        this.form.patchValue({
          authMode: res.data.authMode,
          businessModel: res.data.businessModel ?? 'GATEWAY',
          accountId: res.data.accountId ?? '',
          merchantId: res.data.merchantId ?? '',
        });
        this.loading.set(false);
      },
      error: (err) => {
        if (err?.status === 404) {
          this.loading.set(false);
          return;
        }
        this.error.set(err?.error?.message ?? 'Falha ao carregar credenciais');
        this.loading.set(false);
      },
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    this.stoneApi.saveCredentials(this.form.getRawValue()).subscribe({
      next: (res) => {
        this.credentials.set(res.data);
        this.success.set('Credenciais salvas com sucesso');
        this.form.patchValue({ clientSecret: '' });
        this.saving.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao salvar');
        this.saving.set(false);
      },
    });
  }

  testConnection(): void {
    this.testing.set(true);
    this.error.set(null);
    this.success.set(null);

    this.stoneApi.testConnection().subscribe({
      next: (res) => {
        this.success.set(res.data.message ?? 'Conexão OK');
        this.testing.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha no teste de conexão');
        this.testing.set(false);
      },
    });
  }
}
