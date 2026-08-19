import { Component, OnInit, inject, signal } from '@angular/core';

import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BankIntegrationApiService } from '../../core/services/api.services';

import { BankIntegration, BankProviderCatalogItem } from '../../core/models/api.models';

import {

  buildCredentialForm,

  buildIntegrationPayload,

  applyIntegrationToFormValues,

  integrationReserveNotice,

  schemaFields,

  validateCredentialForm,

} from '../../core/utils/credential-form.util';
import { bankProvidersMatch, normalizeBankProviderCode } from '../../core/utils/bank-provider.util';

import { DynamicCredentialFormComponent } from './dynamic-credential-form/dynamic-credential-form.component';



@Component({

  selector: 'app-integration-detail-page',

  standalone: true,

  imports: [ReactiveFormsModule, RouterLink, DynamicCredentialFormComponent],

  templateUrl: './integration-detail-page.component.html',

  styleUrl: './integration-detail-page.component.scss',

})

export class IntegrationDetailPageComponent implements OnInit {

  private readonly fb = inject(FormBuilder);

  private readonly route = inject(ActivatedRoute);

  private readonly router = inject(Router);

  private readonly bankApi = inject(BankIntegrationApiService);



  readonly provider = signal('STONE');

  readonly catalogItem = signal<BankProviderCatalogItem | null>(null);

  readonly loading = signal(true);

  readonly saving = signal(false);

  readonly testing = signal(false);

  readonly syncing = signal(false);
  readonly removing = signal(false);

  readonly error = signal<string | null>(null);

  readonly success = signal<string | null>(null);

  readonly configured = signal(false);



  form: FormGroup = this.fb.group({});



  ngOnInit(): void {

    const provider = normalizeBankProviderCode(this.route.snapshot.paramMap.get('provider') ?? 'STONE');

    this.provider.set(provider);



    this.bankApi.listCatalog().subscribe({

      next: (catalogRes) => {

        const item =

          catalogRes.data.find((entry) => bankProvidersMatch(entry.provider, provider)) ?? null;

        this.catalogItem.set(item);

        if (item) {

          this.form = buildCredentialForm(this.fb, item.credentialSchema);

        }



        this.bankApi.getCredentials(provider).subscribe({

          next: (res) => {

            this.applyIntegration(res.data, item);

            this.configured.set(true);

            this.loading.set(false);

          },

          error: () => {

            if (item) {

              this.form.reset(applyIntegrationToFormValues(item.credentialSchema, {}));

            }

            this.loading.set(false);

          },

        });

      },

      error: () => {

        this.loading.set(false);

      },

    });

  }



  save(): void {

    const item = this.catalogItem();

    const values = this.form.getRawValue() as Record<string, string>;

    const validationError = validateCredentialForm(item?.credentialSchema, values);

    if (this.form.invalid || validationError) {

      this.form.markAllAsTouched();

      this.error.set(validationError ?? 'Preencha os campos obrigatórios');

      return;

    }



    this.saving.set(true);

    this.error.set(null);

    this.success.set(null);



    const payload = buildIntegrationPayload(item?.credentialSchema, values);

    if (!this.configured() && !payload.clientSecret) {

      this.error.set('Informe a chave secreta no primeiro cadastro');

      this.saving.set(false);

      return;

    }



    this.bankApi.saveCredentials(this.provider(), payload).subscribe({

      next: (res) => {

        this.applyIntegration(res.data, item);

        this.configured.set(true);

        this.success.set('Integração salva com sucesso');

        if (this.form.contains('clientSecret')) {

          this.form.patchValue({ clientSecret: '' });

        }

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



    this.bankApi.testConnection(this.provider()).subscribe({

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



  providerTitle(): string {

    return this.catalogItem()?.displayName ?? this.provider();

  }



  supportsConnectionTest(): boolean {

    return this.catalogItem()?.supportsConnectionTest ?? false;

  }



  supportsSync(): boolean {

    return this.catalogItem()?.supportsSync ?? false;

  }



  reserveNotice(): string | null {

    return integrationReserveNotice(this.catalogItem());

  }



  credentialFields() {

    return schemaFields(this.catalogItem()?.credentialSchema);

  }



  syncPix(): void {

    this.syncing.set(true);

    this.error.set(null);

    this.success.set(null);



    this.bankApi.sync(this.provider()).subscribe({

      next: (res) => {

        const data = res.data;

        this.success.set(

          `Sincronização concluída: ${data.imported} importados, ${data.reconciled} conciliados (${data.fetched} consultados)`,

        );

        this.syncing.set(false);

      },

      error: (err) => {

        this.error.set(err?.error?.message ?? 'Falha na sincronização');

        this.syncing.set(false);

      },

    });

  }



  removeIntegration(): void {

    const name = this.providerTitle();

    const confirmed = window.confirm(

      `Remover a integração com ${name}? Você poderá cadastrá-la novamente depois.`,

    );

    if (!confirmed) {

      return;

    }



    this.removing.set(true);

    this.error.set(null);

    this.success.set(null);



    this.bankApi.remove(this.provider()).subscribe({
      next: () => {
        this.router.navigate(['/integrations']);
      },
      error: (err) => {
        const status = err?.status;
        if (status === 404 || status === 500) {
          this.error.set(
            'Remoção indisponível no backend atual. Execute docker compose up -d --build na pasta confiapix.',
          );
        } else {
          this.error.set(err?.error?.message ?? 'Falha ao remover integração');
        }
        this.removing.set(false);
      },
    });

  }



  private applyIntegration(data: BankIntegration, item: BankProviderCatalogItem | null): void {
    this.form.patchValue(applyIntegrationToFormValues(item?.credentialSchema, data));
  }
}


